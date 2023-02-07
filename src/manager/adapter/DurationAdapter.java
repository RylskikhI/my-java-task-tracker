package manager.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(final JsonWriter jsonWriter, final Duration localDate) throws IOException {
        jsonWriter.value(localDate.getSeconds());
    }

    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        String s = jsonReader.nextString();
        long seconds = Long.parseLong(s);
        return Duration.of(seconds, ChronoUnit.SECONDS);
    }
}
