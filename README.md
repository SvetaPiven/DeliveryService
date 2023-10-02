# DeliveryService

delivery service обращается к user service за информацией о курьере:
Ep на user service /find_courier

на что должны получить ответ:
{
    "courierId": 1
}

Добавляем курьера в таблицу delivery, status меняем на “Курьер найден”.
Добавляем возможность для курьера изменять статус доставки по эндпойнту:
        Ep на delivery service /set_status

        {
    "deliveryStatus": “Собрано”
        }

Также такие варианты: “Отправлено в доставку”, “Прибыл на место”, “Получено”