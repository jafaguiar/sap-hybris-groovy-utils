import de.hybris.platform.core.model.order.OrderModel
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult

import com.psp.payment.maintenance.services.PspMaintenanceService

orderCode = ""
amountToRefund = 0.00

try {
    FlexibleSearchService flexibleSearchService = context.getBean("flexibleSearchService")
    PspMaintenanceService pspMaintenanceService = context.getBean("pspMaintenanceService")
    String queryString = "SELECT {pk} FROM {Order AS or} WHERE ({or:versionID} IS NULL OR {or:versionID} LIKE '') AND {or:code} = '" + orderCode+"'" 

    FlexibleSearchQuery query = new FlexibleSearchQuery(queryString)
    SearchResult<OrderModel> orderSearchResult = flexibleSearchService.search(query)
    List<OrderModel> orders = orderSearchResult.getResult()

    if (orders.size() >= 1) {
        orderModel = orders.get(0)
        println "Order: ${orderModel.getCode()} | Amount refunded: ${amountToRefund}"
        pspMaintenanceService.refundFunds(orderModel, BigDecimal.valueOf(amountToRefund))
    } else {
        println "Order $orderCode does not exist"
    }
} catch (Exception e) {
    println "Failed with the error: " + e.getMessage()
}
