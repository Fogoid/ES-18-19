package pt.ulisboa.tecnico.softeng.broker.presentation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pt.ulisboa.tecnico.softeng.broker.services.local.BrokerInterface;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.BrokerData.CopyDepth;
import pt.ulisboa.tecnico.softeng.broker.services.local.dataobjects.RoomBookingData;

@Controller
@RequestMapping(value = "/brokers/{brokerCode}/bulks/{bulkId}/{reference}/roomBooking")
public class RoomBookingController {

    private static Logger logger = LoggerFactory.getLogger(RoomBookingController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String showBookingDetails(Model model, @PathVariable String brokerCode, @PathVariable String bulkId,
                                     @PathVariable String reference) {

        BrokerData brokerData = BrokerInterface.getBrokerDataByCode(brokerCode, CopyDepth.BULKS);
        RoomBookingData roomBookingData = BrokerInterface.roomBookingInformation(brokerCode, bulkId, reference);

        if (roomBookingData == null) {
            model.addAttribute("data", new RoomBookingData());
            model.addAttribute("broker", brokerData);
            return "bulks";
        } else {
            model.addAttribute("data", roomBookingData);
            model.addAttribute("broker", brokerData);
            return "roomBooking";
        }
    }
}
