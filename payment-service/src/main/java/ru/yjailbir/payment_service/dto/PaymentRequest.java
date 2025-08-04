package ru.yjailbir.payment_service.dto;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.lang.Nullable;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * PaymentRequest
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-04T21:17:01.787026800+03:00[Europe/Moscow]", comments = "Generator version: 7.12.0")
public class PaymentRequest {

  private String userId;

  private Double amount;

  public PaymentRequest() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentRequest(String userId, Double amount) {
    this.userId = userId;
    this.amount = amount;
  }

  public PaymentRequest userId(String userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Идентификатор пользователя
   * @return userId
   */
  @NotNull 
  @Schema(name = "userId", description = "Идентификатор пользователя", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("userId")
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public PaymentRequest amount(Double amount) {
    this.amount = amount;
    return this;
  }

  /**
   * Сумма для списания
   * minimum: 0
   * @return amount
   */
  @NotNull @DecimalMin("0") 
  @Schema(name = "amount", description = "Сумма для списания", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("amount")
  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentRequest paymentRequest = (PaymentRequest) o;
    return Objects.equals(this.userId, paymentRequest.userId) &&
        Objects.equals(this.amount, paymentRequest.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, amount);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentRequest {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

