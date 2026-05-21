package vn.tt.practice.orderservice.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.tt.practice.orderservice.dto.ProductDTO;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/products/{id}")
    ResponseEntity<ProductDTO> getProductById(@PathVariable("id") String productId);

    @PutMapping("/products/{id}/decrease-quantity")
    ResponseEntity<String> decreaseQuantity(@PathVariable("id") String productId,
                                            @RequestParam("amount") int amount);
}

