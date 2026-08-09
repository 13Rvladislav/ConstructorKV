package com.example.constructorKV.kvtable.dto.response;

import com.example.constructorKV.common.annotation.ExternalEntity;
import com.example.constructorKV.common.annotation.ExternalField;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ExternalEntity(
        name = "KvTableRow",
        displayName = "Строка таблицы КВ",
        description = "Строка сводной таблицы комплектовочных ведомостей"
)
public class KvTableRowDto {

    @ExternalField(displayName = "№", displayOrder = 1, isRequired = true)
    private Integer number;

    @ExternalField(
            displayName = "ID служебной записки",
            isKey = true,
            isNavigation = true,
            relatedEntity = "SlZapiska",
            category = "Идентификаторы",
            displayOrder = 2,
            isRequired = true
    )
    private Integer slZapiskaId;

    @ExternalField(
            displayName = "Служебная записка",
            displayOrder = 3,
            isRequired = true
    )
    private String slZapiskaObozn;

    @ExternalField(
            displayName = "ID КВ",
            isNavigation = true,
            relatedEntity = "Komplektovka",
            category = "Идентификаторы",
            displayOrder = 4,
            isRequired = false    // ← скрыто в UI
    )
    private Integer komplektovkaId;

    @ExternalField(
            displayName = "Комплектовочная ведомость",
            displayOrder = 5,
            isRequired = true
    )
    private String komplektovkaDesign;

    @ExternalField(
            displayName = "ID сборки",
            isNavigation = true,
            relatedEntity = "Specif22",
            category = "Идентификаторы",
            displayOrder = 6,
            isRequired = false    // ← скрыто в UI
    )
    private Integer specifId;

    @ExternalField(
            displayName = "Дата согласования СЗ",
            category = "Даты",
            displayOrder = 7,
            isRequired = true
    )
    private String slZapAgreedDate;

    @ExternalField(
            displayName = "Статус КВ",
            displayOrder = 8,
            isRequired = true
    )
    private String status;

    @ExternalField(
            displayName = "Декомпозиция (родительская КВ)",
            isNavigation = true,
            relatedEntity = "Komplektovka",
            category = "Связи",
            displayOrder = 9,
            isRequired = true    // ← скрыто в UI
    )
    private String parentKvDesign;

    @ExternalField(
            displayName = "ID статуса КВ",
            isNavigation = true,
            relatedEntity = "KvStatusDict",
            category = "Идентификаторы",
            displayOrder = 10,
            isRequired = false    // ← скрыто в UI
    )
    private Integer kvStatusId;

    @ExternalField(
            displayName = "СЗ согласована",
            category = "Статусы",
            displayOrder = 11,
            isRequired = false    // ← скрыто в UI
    )
    private Boolean isSlZapAgreed;

    @ExternalField(
            displayName = "КВ существует",
            category = "Статусы",
            displayOrder = 12,
            isRequired = false    // ← скрыто в UI
    )
    private Boolean hasKv;
}