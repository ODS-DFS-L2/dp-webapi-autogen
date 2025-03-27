package net.ouranos.domain.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * データモデル要素
 */

@Schema(name = "dataModel_parts_attribute", description = "データモデル要素")
@JsonTypeName("dataModel_parts_attribute")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-10T18:31:43.516109700+09:00[Asia/Tokyo]", comments = "Generator version: 7.6.0")
public class DataModelPartsAttribute {

  private UUID dataId;

  private BigDecimal value;

  public DataModelPartsAttribute() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public DataModelPartsAttribute(UUID dataId, BigDecimal value) {
    this.dataId = dataId;
    this.value = value;
  }

  public DataModelPartsAttribute dataId(UUID dataId) {
    this.dataId = dataId;
    return this;
  }

  /**
   * データを一意に識別するID
   * @return dataId
  */
  @NotNull @Valid 
  @Schema(name = "dataId", example = "78aa302c-1600-44b3-a331-e4659c0b28a1", description = "データを一意に識別するID", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("dataId")
  public UUID getDataId() {
    return dataId;
  }

  public void setDataId(UUID dataId) {
    this.dataId = dataId;
  }

  public DataModelPartsAttribute value(BigDecimal value) {
    this.value = value;
    return this;
  }

  /**
   * データモデル要素値
   * minimum: 0
   * maximum: 999999999
   * @return value
  */
  @NotNull @Valid @DecimalMin("0") @DecimalMax("999999999") 
  @Schema(name = "value", example = "123456789", description = "データモデル要素値", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("value")
  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DataModelPartsAttribute dataModelPartsAttribute = (DataModelPartsAttribute) o;
    return Objects.equals(this.dataId, dataModelPartsAttribute.dataId) &&
        Objects.equals(this.value, dataModelPartsAttribute.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataId, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class DataModelPartsAttribute {\n");
    sb.append("    dataId: ").append(toIndentedString(dataId)).append("\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
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

