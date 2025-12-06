package pl.msz.apc.config;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.AbstractEmbeddingModel;
import org.springframework.ai.embedding.EmbeddingOptions;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingResponseMetadata;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.OpenAiApi.EmbeddingList;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.Assert;

import java.util.List;

/**
 * A custom EmbeddingModel implementation to handle Gemini's OpenAI-compatible API
 * which might return null 'usage' fields, causing NPEs in the standard OpenAiEmbeddingModel.
 */
public class GeminiEmbeddingModel extends AbstractEmbeddingModel {

    private final OpenAiApi openAiApi;
    private final OpenAiEmbeddingOptions defaultOptions;
    private final RetryTemplate retryTemplate;
    private final MetadataMode metadataMode;

    public GeminiEmbeddingModel(OpenAiApi openAiApi) {
        this(openAiApi, MetadataMode.EMBED, OpenAiEmbeddingOptions.builder().withModel("text-embedding-004").build(), RetryUtils.DEFAULT_RETRY_TEMPLATE);
    }

    public GeminiEmbeddingModel(OpenAiApi openAiApi, MetadataMode metadataMode, OpenAiEmbeddingOptions options, RetryTemplate retryTemplate) {
        Assert.notNull(openAiApi, "OpenAiApi must not be null");
        Assert.notNull(metadataMode, "MetadataMode must not be null");
        Assert.notNull(options, "Options must not be null");
        Assert.notNull(retryTemplate, "RetryTemplate must not be null");

        this.openAiApi = openAiApi;
        this.metadataMode = metadataMode;
        this.defaultOptions = options;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public List<Double> embed(Document document) {
        Assert.notNull(document, "Document must not be null");
        return this.embed(document.getFormattedContent(this.metadataMode));
    }

    @Override
    public EmbeddingResponse call(EmbeddingRequest request) {
        OpenAiEmbeddingOptions requestOptions = mergeOptions(request.getOptions(), this.defaultOptions);
        
        OpenAiApi.EmbeddingRequest apiRequest = new OpenAiApi.EmbeddingRequest(
                request.getInstructions(),
                requestOptions.getModel(),
                requestOptions.getEncodingFormat(),
                requestOptions.getDimensions(),
                requestOptions.getUser()
        );

        return this.retryTemplate.execute(ctx -> {
            org.springframework.http.ResponseEntity<EmbeddingList<OpenAiApi.Embedding>> apiResponse = 
                    this.openAiApi.embeddings(apiRequest);

            if (apiResponse == null || apiResponse.getBody() == null) {
                return new EmbeddingResponse(List.of());
            }

            EmbeddingList<OpenAiApi.Embedding> body = apiResponse.getBody();

            List<org.springframework.ai.embedding.Embedding> embeddings = body.data().stream()
                    .map(e -> new org.springframework.ai.embedding.Embedding(e.embedding(), e.index()))
                    .toList();

            org.springframework.ai.chat.metadata.Usage usage = null;
            if (body.usage() != null) {
                usage = new org.springframework.ai.chat.metadata.Usage() {
                    @Override
                    public Long getPromptTokens() {
                        return body.usage().promptTokens().longValue();
                    }

                    @Override
                    public Long getGenerationTokens() {
                        return 0L;
                    }

                    @Override
                    public Long getTotalTokens() {
                        return body.usage().totalTokens().longValue();
                    }
                };
            }
            
            EmbeddingResponseMetadata metadata = new EmbeddingResponseMetadata();
            metadata.put("model", requestOptions.getModel());
            if (usage != null) {
                metadata.put("usage", usage);
            }
            return new EmbeddingResponse(embeddings, metadata);
        });
    }

    private OpenAiEmbeddingOptions mergeOptions(EmbeddingOptions requestOptions, OpenAiEmbeddingOptions defaultOptions) {
        OpenAiEmbeddingOptions.Builder builder = OpenAiEmbeddingOptions.builder();
        if (defaultOptions != null) {
            builder.withModel(defaultOptions.getModel());
            builder.withDimensions(defaultOptions.getDimensions());
            builder.withUser(defaultOptions.getUser());
            builder.withEncodingFormat(defaultOptions.getEncodingFormat());
        }
        
        if (requestOptions != null) {
            OpenAiEmbeddingOptions options = ModelOptionsUtils.merge(requestOptions, builder.build(), OpenAiEmbeddingOptions.class);
            return options;
        }

        return builder.build();
    }
}
