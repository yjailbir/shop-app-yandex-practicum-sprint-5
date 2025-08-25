package ru.yjailbir.payment_service.dto;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.processing.Generated;


/**
 * PaymentResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-04T21:17:01.787026800+03:00[Europe/Moscow]", comments = "Generator version: 7.12.0")
public class PaymentResponse {

  private Boolean success;

  private Long remainingBalance;

  private @Nullable String message;

  public PaymentResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public PaymentResponse(Boolean success, Long remainingBalance) {
    this.success = success;
    this.remainingBalance = remainingBalance;
  }

  public PaymentResponse success(Boolean success) {
    this.success = success;
    return this;
  }

  /**
   * Успешность платежа
   * @return success
   */
  @NotNull
  @Schema(name = "success", description = "Успешность платежа", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("success")
  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public PaymentResponse remainingBalance(Long remainingBalance) {
    this.remainingBalance = remainingBalance;
    return this;
  }

  /**
   * Оставшийся баланс
   * @return remainingBalance
   */
  @NotNull 
  @Schema(name = "remainingBalance", description = "Оставшийся баланс", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("remainingBalance")
  public Long getRemainingBalance() {
    return remainingBalance;
  }

  public void setRemainingBalance(Long remainingBalance) {
    this.remainingBalance = remainingBalance;
  }

  public PaymentResponse message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Дополнительная информация
   * @return message
   */
  
  @Schema(name = "message", description = "Дополнительная информация", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("message")
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PaymentResponse paymentResponse = (PaymentResponse) o;
    return Objects.equals(this.success, paymentResponse.success) &&
        Objects.equals(this.remainingBalance, paymentResponse.remainingBalance) &&
        Objects.equals(this.message, paymentResponse.message);
  }

  @Override
  public int hashCode() {
    return Objects.hash(success, remainingBalance, message);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PaymentResponse {\n");
    sb.append("    success: ").append(toIndentedString(success)).append("\n");
    sb.append("    remainingBalance: ").append(toIndentedString(remainingBalance)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
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

