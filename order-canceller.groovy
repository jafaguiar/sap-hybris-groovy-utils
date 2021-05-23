import de.hybris.platform.servicelayer.search.*; 
import de.hybris.platform.basecommerce.enums.*;
import de.hybris.platform.sap.sapmodel.enums.*;
import de.hybris.platform.core.enums.*;

codPedidos=["E001000xxx"]

queryPedido = new FlexibleSearchQuery("SELECT {o:pk} FROM {Order AS o} WHERE {o:code} IN (?codes) AND ({o:versionID} IS NULL)")
queryPedido.addQueryParameter("codes", codPedidos)
pedidos = flexibleSearchService.search(queryPedido).getResult()

pedidos.each { pedido ->
    
    pedido.orderProcess.each { process ->
        if (process.processDefinitionName.equals("sap-oms-order-process")){
            businessProcessService.restartProcess(process,"cancelled")
        }
    }
    
    pedido.consignments.each { consignment ->
        consignment.status = ConsignmentStatus.CANCELLED
        consignment.consignmentEntries.each { consignmentEntry ->
            consignmentEntry.quantity = 0
            consignmentEntry.status = ConsignmentEntryStatus.CANCELLED
            modelService.save(consignmentEntry)
        }
        modelService.save(consignment)
    }
    
    pedido.status = OrderStatus.CANCELLED
    pedido.setName("CANCELLED-BY");
    modelService.save(pedido)
}
