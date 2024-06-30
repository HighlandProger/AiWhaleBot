package ru.rusguardian.bot.command.service.commands.admin.reports.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.rusguardian.bot.command.service.Command;
import ru.rusguardian.bot.command.service.CommandName;
import ru.rusguardian.domain.Order;
import ru.rusguardian.service.data.OrderService;
import ru.rusguardian.telegram.bot.util.util.telegram_message.ReplyMarkupUtil;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetOrdersReportCommand extends Command {

    private final OrderService orderService;
    @Override
    public CommandName getType() {
        return CommandName.GET_ORDERS_REPORT;
    }

    @Override
    protected void mainExecute(Update update) throws TelegramApiException {
        List<Order> purchasedOrders = orderService.findAllPurchasedOrders();
        Double incomeFull = purchasedOrders.stream().mapToDouble(Order::getPrice).sum();
        Double incomeWeek = purchasedOrders.stream()
                .filter(e -> e.getPurchasedAt()!=null && e.getPurchasedAt().plusWeeks(1L).isAfter(LocalDateTime.now()))
                .mapToDouble(Order::getPrice).sum();

        String text = getText(incomeFull, incomeWeek);

        editMessage(update, text, ReplyMarkupUtil.getInlineKeyboard(getButtons()));
    }

    private String getText(Double incomeFull, Double incomeWeek){
        String pattern = """
                Отчет по заказам:
                
                Общая сумма заказов: {0}$
                Сумма заказов за неделю: {1}$
                """;

        return MessageFormat.format(pattern, incomeFull, incomeWeek);
    }

    private String[][][] getButtons(){
        return new String[][][]{
                {{CommandName.BACK.getViewName(), CommandName.CHOOSE_REPORT.getBlindName()}}
        };
    }
}
