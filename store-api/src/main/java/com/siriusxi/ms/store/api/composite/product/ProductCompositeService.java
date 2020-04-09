package com.siriusxi.ms.store.api.composite.product;

import com.siriusxi.ms.store.api.composite.product.dto.ProductAggregate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("v1")
@Api("REST API for composite product information.")
public interface ProductCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/v1/product-composite/1
     *
     * @param productId is the product that you are looking for.
     * @return the composite product info, if found, else null.
     */
    @ApiOperation(
            value = "${api.product-composite.get-composite-product.description}",
            notes = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. " +
                    "See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the " +
                    "processing to fails. See response message for more information.")
    })
    @GetMapping(
            value = "/product-composite/{productId}",
            produces = APPLICATION_JSON_VALUE)
    ProductAggregate getProduct(@PathVariable int productId);
}
