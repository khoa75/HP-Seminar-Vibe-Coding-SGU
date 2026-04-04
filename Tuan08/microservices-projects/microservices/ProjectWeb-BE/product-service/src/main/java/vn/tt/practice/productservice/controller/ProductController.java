package vn.tt.practice.productservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.tt.practice.productservice.dto.ProductDTO;
import vn.tt.practice.productservice.model.Product;
import vn.tt.practice.productservice.repository.ProductRepo;
import vn.tt.practice.productservice.service.ProductService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final ProductRepo productRepo;

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {

        return ResponseEntity.ok(productRepo.findById(id).orElse(null));
    } 

    private Page<ProductDTO> getAllProducts(Pageable pageable) {
        Page<Product> products = productRepo.findAll(pageable);

        return products.map(product -> ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .image(product.getImage())
                .productCode(product.getProductCode())
                .quantity(product.getQuantity())
                .build());
    }

    @GetMapping
    public ResponseEntity<Page<ProductDTO>> getProducts(
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "9") int size) {

            int adjustedPage = (page > 0) ? page : 0; 

            Pageable pageable = PageRequest.of(adjustedPage, size);
            Page<ProductDTO> products = productService.getAllProducts(pageable);

            return ResponseEntity.ok(products);
        }

    @PutMapping("/{id}/decrease-quantity")
    public ResponseEntity<?> decreaseQuantity(@PathVariable String id, @RequestParam int amount) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < amount) {
            return ResponseEntity.badRequest().body("Not enough quantity");
        }

        product.setQuantity(product.getQuantity() - amount);
        productRepo.save(product);

        return ResponseEntity.ok("Quantity updated");
    }

    @PostMapping("")
    public ResponseEntity<String> addProducts(@RequestBody ProductDTO productDTO) {
        productService.createProduct(productDTO);
        return ResponseEntity.ok().body("Add Product successfull");
    }

    @PutMapping("/{id}/add-to-cart")
    public ResponseEntity<ProductDTO> addToCart(@PathVariable String id) {
        return ResponseEntity.ok(productService.addToCart(id));
    }

    @PutMapping("/{id}/remove-from-cart")
    public ResponseEntity<ProductDTO> removeFromCart(@PathVariable String id) {
        return ResponseEntity.ok(productService.removeFromCart(id));
    }
}
