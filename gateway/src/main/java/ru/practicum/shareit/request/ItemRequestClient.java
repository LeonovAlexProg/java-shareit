package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addNewRequest(long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }


    public ResponseEntity<Object> getRequest(long userId, long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> getUserRequests(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequests(long userId, Integer from, Integer size) {
        Map<String, Object> parameters;
        if (from != null && size != null) {
            parameters = Map.of(
                    "from", from,
                    "size", size
            );
        } else if (from == null && size != null) {
            parameters = Map.of(
                    "size", size
            );
        } else if (from != null){
            parameters = Map.of(
                    "from", from
            );
        } else {
            parameters = Map.of();
        }

        if (parameters.containsKey("from") && parameters.containsKey("size")) {
            return get("/all?from={from}&size={size}", userId, parameters);
        } else if (!parameters.containsKey("from") && parameters.containsKey("size")) {
            return get("/all?size={size}", userId, parameters);
        } else if (parameters.containsKey("from")) {
            return get("/all?from={from}", userId, parameters);
        } else {
            return get("/all", userId);
        }
    }
}