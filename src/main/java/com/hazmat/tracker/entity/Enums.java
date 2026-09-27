package com.hazmat.tracker.entity;

public class Enums {

    public enum ContactType { CUSTOMER, VENDOR }

    public enum ProductType { SERVICE, GOODS }

    public enum AccountType { ASSET, LIABILITY, INCOME, EXPENSE, EQUITY }

    public enum JournalType { SALES, PURCHASE, CASH, BANK }

    public enum OrderStatus { DRAFT, CONFIRMED, BILLED_OR_INVOICED, PAID, CANCELLED }

    public enum PaymentMethod { CASH, BANK }

    public enum PaymentDirection { INBOUND, OUTBOUND }

    public enum InspectionStatus { SCHEDULED, COMPLETED, OVERDUE, CANCELLED }
}
