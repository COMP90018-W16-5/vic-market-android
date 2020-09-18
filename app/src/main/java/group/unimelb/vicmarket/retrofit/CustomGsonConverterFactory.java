package group.unimelb.vicmarket.retrofit;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import group.unimelb.vicmarket.common.AppConstant;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import retrofit2.Converter;
import retrofit2.Retrofit;

public final class CustomGsonConverterFactory extends Converter.Factory {

    private static final String TAG = "GsonConverterFactory";
    private final Gson gson;

    private CustomGsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    public static CustomGsonConverterFactory create() {
        return create(new Gson());
    }

    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static CustomGsonConverterFactory create(Gson gson) {
        if (gson == null) throw new NullPointerException("gson == null");
        return new CustomGsonConverterFactory(gson);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new CustomGsonResponseConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyConverter<>(gson, adapter);
    }

    static class CustomGsonResponseConverter<T> implements Converter<ResponseBody, T> {

        private final TypeAdapter<T> adapter;

        CustomGsonResponseConverter(Gson gson, TypeAdapter<T> adapter) {
            this.adapter = adapter;
        }

        @Override
        public T convert(@NotNull ResponseBody value) {
            try {
                String body = value.string();
                if (AppConstant.PRINT_JSON_DATA) {
                    Log.d(TAG, body);
                }
                return adapter.fromJson(body);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            } finally {
                value.close();
            }
        }
    }

    static class GsonRequestBodyConverter<T> implements Converter<T, RequestBody> {
        private final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");
        private final Charset UTF_8 = StandardCharsets.UTF_8;

        private final Gson gson;
        private final TypeAdapter<T> adapter;

        GsonRequestBodyConverter(Gson gson, TypeAdapter<T> adapter) {
            this.gson = gson;
            this.adapter = adapter;
        }

        @Override
        public RequestBody convert(@NotNull T value) throws IOException {
            Buffer buffer = new Buffer();
            Writer writer = new OutputStreamWriter(buffer.outputStream(), UTF_8);
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            adapter.write(jsonWriter, value);
            jsonWriter.close();
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
        }
    }
}