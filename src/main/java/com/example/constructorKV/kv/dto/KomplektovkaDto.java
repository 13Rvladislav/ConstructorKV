package com.example.constructorKV.kv.dto;

import com.example.constructorKV.common.annotation.ExternalEntity;
import com.example.constructorKV.common.annotation.ExternalField;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ExternalEntity(
        name = "Komplektovka",
        displayName = "Комплектовочная ведомость",
        description = "Сущность комплектовочной ведомости"
)
public class KomplektovkaDto {

    @ExternalField(
            displayName = "ID",
            isKey = true,
            category = "Идентификаторы",
            displayOrder = 1,
            isRequired = true
    )
    private Integer id;

    @ExternalField(
            displayName = "Обозначение",
            displayOrder = 2,
            isRequired = true
    )
    private String design;

    @ExternalField(
            displayName = "ID служебной записки",
            isNavigation = true,
            relatedEntity = "SlZapiska",
            category = "Идентификаторы",
            displayOrder = 3,
            isRequired = true
    )
    private Integer idSz;

    @ExternalField(
            displayName = "Сборки",
            displayOrder = 4,
            isRequired = false
    )
    private String sborkiSz;

    @ExternalField(
            displayName = "Дата создания",
            category = "Даты",
            displayOrder = 5,
            isRequired = true
    )
    private String dateCreate;

    @ExternalField(
            displayName = "Дата архивации",
            category = "Даты",
            displayOrder = 6,
            isRequired = false
    )
    private String dateArchiving;

    @ExternalField(
            displayName = "Статус",
            displayOrder = 7,
            isRequired = true
    )
    private String status;

    @ExternalField(
            displayName = "Кем сформирована",
            isNavigation = true,
            relatedEntity = "Person",
            category = "Пользователи",
            displayOrder = 8,
            isRequired = false
    )
    private Integer formedBy;

    @ExternalField(
            displayName = "Кем согласована",
            isNavigation = true,
            relatedEntity = "Person",
            category = "Пользователи",
            displayOrder = 9,
            isRequired = false
    )
    private Integer approvedBy;

    @ExternalField(
            displayName = "Дата согласования",
            category = "Даты",
            displayOrder = 10,
            isRequired = false
    )
    private String approvedDate;

    @ExternalField(
            displayName = "Родительская КВ (декомпозиция)",
            isNavigation = true,
            relatedEntity = "Komplektovka",
            category = "Связи",
            displayOrder = 11,
            isRequired = false
    )
    private Integer parentKvId;

    @ExternalField(
            displayName = "ID статуса КВ",
            isNavigation = true,
            relatedEntity = "KvStatusDict",
            category = "Идентификаторы",
            displayOrder = 12,
            isRequired = false
    )
    private Integer kvStatusId;
}