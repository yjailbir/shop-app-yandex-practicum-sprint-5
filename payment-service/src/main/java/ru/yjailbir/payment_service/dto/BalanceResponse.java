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
 * BalanceResponse
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-08-04T21:17:01.787026800+03:00[Europe/Moscow]", comments = "Generator version: 7.12.0")
public class BalanceResponse {

  private String userId;

  private Long balance;

  public BalanceResponse() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public BalanceResponse(String userId, Long balance) {
    this.userId = userId;
    this.balance = balance;
  }

  public BalanceResponse userId(String userId) {
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

  public BalanceResponse balance(Long balance) {
    this.balance = balance;
    return this;
  }

  /**
   * Текущий баланс
   * @return balance
   */
  @NotNull 
  @Schema(name = "balance", description = "Текущий баланс", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("balance")
  public Long getBalance() {
    return balance;
  }

  public void setBalance(Long balance) {
    this.balance = balance;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BalanceResponse balanceResponse = (BalanceResponse) o;
    return Objects.equals(this.userId, balanceResponse.userId) &&
        Objects.equals(this.balance, balanceResponse.balance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, balance);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BalanceResponse {\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    balance: ").append(toIndentedString(balance)).append("\n");
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

