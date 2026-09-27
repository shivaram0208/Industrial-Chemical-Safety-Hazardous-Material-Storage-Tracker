# Industrial Chemical Safety & Hazardous Material Storage Tracker

Spring Boot + MySQL REST API implementing the full spec:
master data (Contacts, Products, Chart of Accounts, Journals), the Hazmat
storage/expiry module (Vaults, Intake, Safety Inspections), the
Purchase (PO → Vendor Bill → Payment) and Sales (SO → Customer Invoice →
Payment) flows with automatic double-entry journal posting, an
Analytic Account / Budget module, and Balance Sheet / P&L / Budget
Variance reports.

## 1. Prerequisites

- **JDK 17+**
- **Maven** (or use IntelliJ's bundled Maven)
- **MySQL 8.x** running locally
- **IntelliJ IDEA**
- **Postman**

## 2. Database setup

Create the database (or let the app auto-create it — the JDBC URL already
has `createDatabaseIfNotExist=true`):

```sql
CREATE DATABASE hazmat_tracker;
```

Edit `src/main/resources/application.properties` if your MySQL
username/password differ from `root` / `root`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/hazmat_tracker?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

`spring.jpa.hibernate.ddl-auto=update` means Hibernate creates/updates
all tables automatically on first run — no manual DDL needed.

## 3. Open & run in IntelliJ

1. **File → Open** → select the `hazmat-tracker` folder (the one
   containing `pom.xml`). IntelliJ will detect it as a Maven project and
   download dependencies automatically.
2. Wait for indexing/Maven import to finish.
3. Open `src/main/java/com/hazmat/tracker/HazmatTrackerApplication.java`
   and click the green ▶ Run button (or right-click → Run).
4. On first successful start, watch the console — Hibernate will create
   all tables, and `DataSeeder` will auto-insert:
   - 8 Chart of Accounts entries (Containment Storage Facility,
     Cash/Bank, Chemical Supplier Payables, Safety Vendor Creditors,
     Hazmat Storage Revenue, Compliance Fee Revenue, Chemical Disposal
     Expenses, Facility Safety Operating Costs)
   - 4 Journals (Sales, Purchase, Cash, Bank)
5. The API is now live at `http://localhost:8080`.

*(Command-line alternative, if not using the IDE run button:
`mvn spring-boot:run`)*

## 4. Apache note

Apache HTTP Server is not required to run the Spring Boot app itself —
Spring Boot ships its own embedded Tomcat server on port 8080. If your
assignment specifically requires Apache, use it as a **reverse proxy**
in front of the app (e.g. `ProxyPass "/" "http://localhost:8080/"` in
an Apache vhost config) so requests to port 80 forward to the Spring
Boot service. The REST API itself works identically either way.

## 5. Testing with Postman — end-to-end scenario

Import `postman_collection.json` (included in this project) into
Postman, or fire the requests manually in this order:

### A. Master data
1. `POST /api/contacts` — create a Customer (Chemical Manufacturer) and a Vendor (Neutralization Agent Vendor)
2. `POST /api/products` — create "Hazmat Storage Space" (SERVICE), "Neutralization Agent" (GOODS), "Compliance Safety Inspection" (SERVICE)
   (Chart of Accounts + Journals are already seeded automatically)

### B. Hazmat storage & expiry
3. `POST /api/hazmat/vaults` — create a storage vault/zone
4. `POST /api/hazmat/intake` — log a chemical lot into the vault (checks capacity)
5. `GET /api/hazmat/expiring?days=30` — list lots expiring soon
6. `GET /api/hazmat/expired` — list already-expired lots
7. `POST /api/hazmat/inspections` — schedule a safety inspection
8. `PUT /api/hazmat/inspections/{id}/complete` — mark it completed

### C. Sales flow (bill the chemical manufacturer for storage)
9. `POST /api/sales/orders` — create Sales Order with line items
10. `PUT /api/sales/orders/{id}/confirm`
11. `POST /api/sales/orders/{id}/invoice?invoiceNumber=INV-001` — converts to Customer Invoice **and posts a Sales journal entry** (Debit Storage Facility/Receivable, Credit Storage Revenue)
12. `POST /api/sales/invoices/{id}/pay` — collect payment via Bank, **posts Bank journal entry**

### D. Purchase flow (buy neutralization/safety kits)
13. `POST /api/purchase/orders` — create Purchase Order
14. `PUT /api/purchase/orders/{id}/confirm`
15. `POST /api/purchase/orders/{id}/bill?billNumber=BILL-001` — converts to Vendor Bill, **posts Purchase journal entry**
16. `POST /api/purchase/bills/{id}/pay` — pay via Bank, **posts Bank journal entry**

### E. Budget flow
17. `POST /api/budget/analytic-accounts` — create "Hazardous Materials Depot Sector 4"
18. `POST /api/budget/budgets` — allocate a safety operating budget against that sector
19. `PUT /api/budget/budgets/{id}/record-actual?amount=500` — record actual spend
20. `GET /api/reports/budget-variance` — see allocated vs actual vs variance

### F. Reports
21. `GET /api/reports/balance-sheet`
22. `GET /api/reports/profit-and-loss`

## 6. Full API reference

| Module | Method | Endpoint |
|---|---|---|
| Contacts | POST/GET/PUT/DELETE | `/api/contacts`, `/api/contacts/{id}` |
| Products | POST/GET/PUT/DELETE | `/api/products`, `/api/products/{id}` |
| Accounts | POST/GET | `/api/accounts`, `/api/accounts/{id}` |
| Journals | GET | `/api/journals`, `/api/journals/entries` |
| Vaults | POST/GET | `/api/hazmat/vaults`, `/api/hazmat/vaults/{id}` |
| Intake | POST/GET | `/api/hazmat/intake`, `/api/hazmat/vaults/{id}/intakes` |
| Expiry | GET | `/api/hazmat/expiring?days=30`, `/api/hazmat/expired` |
| Inspections | POST/GET/PUT | `/api/hazmat/inspections`, `/api/hazmat/inspections/{id}/complete` |
| Purchase Orders | POST/GET/PUT | `/api/purchase/orders`, `/api/purchase/orders/{id}/confirm` |
| Vendor Bills | POST/GET | `/api/purchase/orders/{id}/bill`, `/api/purchase/bills` |
| Vendor Payments | POST | `/api/purchase/bills/{id}/pay` |
| Sales Orders | POST/GET/PUT | `/api/sales/orders`, `/api/sales/orders/{id}/confirm` |
| Customer Invoices | POST/GET | `/api/sales/orders/{id}/invoice`, `/api/sales/invoices` |
| Customer Payments | POST | `/api/sales/invoices/{id}/pay` |
| Analytic Accounts | POST/GET | `/api/budget/analytic-accounts` |
| Budgets | POST/GET/PUT | `/api/budget/budgets`, `/api/budget/budgets/{id}/record-actual` |
| Reports | GET | `/api/reports/balance-sheet`, `/api/reports/profit-and-loss`, `/api/reports/budget-variance` |

## 7. Project structure

```
src/main/java/com/hazmat/tracker/
├── entity/       JPA entities (Contact, Product, Account, Journal, JournalEntry,
│                  HazmatVault, ChemicalIntake, SafetyInspection, PurchaseOrder,
│                  VendorBill, SalesOrder, CustomerInvoice, Payment,
│                  AnalyticAccount, Budget)
├── repository/   Spring Data JPA repositories
├── service/      Business logic incl. double-entry journal posting
├── controller/   REST controllers
├── dto/          Request DTOs for multi-line transactions
└── config/       DataSeeder (CoA/Journal bootstrap), GlobalExceptionHandler
```

## 8. Design notes / how double-entry posting works

Every money-moving action posts a balanced journal entry via
`AccountingService.postEntry(...)`:

- **Customer Invoice** → Debit `Containment Storage Facility` (1001, Asset) / Credit `Hazmat Storage Revenue` (4001, Income)
- **Customer Payment received** → Debit `Cash/Bank` (1002) / Credit `Containment Storage Facility` (1001)
- **Vendor Bill** → Debit `Chemical Disposal Expenses` (5001, Expense) / Credit `Chemical Supplier Payables` (2001, Liability)
- **Vendor Payment made** → Debit `Chemical Supplier Payables` (2001) / Credit `Cash/Bank` (1002)

Account running balances update automatically, which is what powers the
Balance Sheet and P&L endpoints.
