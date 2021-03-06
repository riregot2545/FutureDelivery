package com.nix.futuredelivery.entity.value.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.nix.futuredelivery.entity.Product;
import com.nix.futuredelivery.entity.Warehouse;
import com.nix.futuredelivery.entity.value.WarehouseProductLine;
import com.nix.futuredelivery.exceptions.NoProductException;
import com.nix.futuredelivery.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class WarehouseProductLineDeserializer extends JsonDeserializer {
    private ProductRepository productRepository;
    public WarehouseProductLineDeserializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        int quantity = node.get("quantity").asInt();
        Long productId = node.get("product").get("id").asLong();
        return new WarehouseProductLine(productRepository.findById(productId)
                .orElseThrow(()->new NoProductException(productId)), quantity);
    }
}
