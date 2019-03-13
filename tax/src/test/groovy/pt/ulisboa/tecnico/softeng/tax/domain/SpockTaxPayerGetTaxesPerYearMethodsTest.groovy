package pt.ulisboa.tecnico.softeng.tax.domain

import org.joda.time.LocalDate


def class SpockTaxPayerGetTaxesPerYearMethodsTest extends SpockRollbackTestAbstractClass {


        def SELLER_NIF = "123456788"
        def BUYER_NIF = "987654311"
        def FOOD = "FOOD"
        def TAX = 10
        LocalDate date = new LocalDate(2018, 02, 13)

        private Seller seller
        private Buyer buyer
        private ItemType itemType


        def populate4Test() {
            IRS irs = IRS.getIRSInstance()
            seller = new Seller(irs, SELLER_NIF, "José Vendido", "Somewhere")
            buyer = new Buyer(irs, BUYER_NIF, "Manuel Comprado", "Anywhere")
            itemType = new ItemType(irs, FOOD, TAX)
        }


        def 'success'() {
            given:
            new Invoice(100, new LocalDate(2017, 12, 12), itemType, seller, buyer)
            new Invoice(100, date, itemType, seller, buyer)
            new Invoice(100, date, itemType, seller, buyer)
            new Invoice(50, date, itemType, seller, buyer)


            when:
            Map<Integer, Double> toPay = seller.getToPayPerYear()
            Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear()

            then:

            2 == toPay.keySet().size()
            10.0d == toPay.get(2017)
            25.0d == toPay.get(2018)

            2 == taxReturn.keySet().size()
            0.5d == taxReturn.get(2017)
            1.25d == taxReturn.get(2018)
        }

        def 'successEmpty'() {

            when:
            Map<Integer, Double> toPay = seller.getToPayPerYear()
            Map<Integer, Double> taxReturn = buyer.getTaxReturnPerYear()


            then:
            0 == toPay.keySet().size()
            0 == taxReturn.keySet().size()
        }

    }
