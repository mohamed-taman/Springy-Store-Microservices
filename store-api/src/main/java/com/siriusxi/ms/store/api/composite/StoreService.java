package com.siriusxi.ms.store.api.composite;

import com.siriusxi.ms.store.api.composite.dto.ProductAggregate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api("REST API for Springy Store products information.")
@RequestMapping("store/api/v1")
public interface StoreService {

  /**
   * Sample usage: curl $HOST:$PORT/store/api/v1/products/1
   *
   * @param productId is the product that you are looking for.
   * @return the product info, if found, else null.
   */
  @ApiOperation(
      value = "${api.product-composite.get-composite-product.description}",
      notes = "${api.product-composite.get-composite-product.notes}")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 400,
            message = """
                    Bad Request, invalid format of the request.
                    See response message for more information.
                    """),
        @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
        @ApiResponse(
            code = 422,
            message = """
                    Unprocessable entity, input parameters caused the processing to fails.
                    See response message for more information.
                    """)
      })
  @GetMapping(value = "products/{productId}",
          produces = APPLICATION_JSON_VALUE)
  ProductAggregate getProduct(@PathVariable int productId);

  /**
   * Sample usage:
   *
   * <p>curl -X POST $HOST:$PORT/store/api/v1/products \
   *    -H "Content-Type: application/json" --data \
   *    '{"productId":123,"name":"product 123", "weight":123}'
   *
   * @param body of product elements definition.
   */
  @ApiOperation(
      value = "${api.product-composite.create-composite-product.description}",
      notes = "${api.product-composite.create-composite-product.notes}")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 400,
            message = """
                Bad Request, invalid format of the request. 
                See response message for more information.
                """),
        @ApiResponse(
            code = 422,
            message = """
                Unprocessable entity, input parameters caused the processing to fail. 
                See response message for more information.
                """)
      })
  @PostMapping(
          value = "products",
          consumes = APPLICATION_JSON_VALUE)
  void createProduct(@RequestBody ProductAggregate body);

  /**
   * Sample usage:
   *
   * <p>curl -X DELETE $HOST:$PORT/store/api/v1/products/1
   *
   * @param productId to delete.
   */
  @ApiOperation(
      value = "${api.product-composite.delete-composite-product.description}",
      notes = "${api.product-composite.delete-composite-product.notes}")
  @ApiResponses(
      value = {
        @ApiResponse(
            code = 400,
            message ="""
                Bad Request, invalid format of the request. 
                See response message for more information.
                """),
        @ApiResponse(
            code = 422,
            message ="""
                Unprocessable entity, input parameters caused the processing to fail. 
                See response message for more information.
                """)
      })
  @DeleteMapping("products/{productId}")
  void deleteProduct(@PathVariable int productId);
}
