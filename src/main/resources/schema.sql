CREATE TABLE APP_USER (
                          USER_ID INT AUTO_INCREMENT PRIMARY KEY,
                          Username VARCHAR(50) NOT NULL UNIQUE,
                          Password_hash VARCHAR(255) NOT NULL,
                          First_name VARCHAR(50) NOT NULL,
                          Last_name VARCHAR(50) NOT NULL,
                          Role VARCHAR(20) NOT NULL,
                          CONSTRAINT chk_user_role
                              CHECK (Role IN ('TAILOR', 'CUSTOMER'))
);

CREATE TABLE USER_PHONE (
                            USER_ID INT NOT NULL,
                            Phone_Number VARCHAR(20) NOT NULL,
                            PRIMARY KEY (USER_ID, Phone_Number),
                            CONSTRAINT fk_phone_user
                                FOREIGN KEY (USER_ID)
                                    REFERENCES APP_USER(USER_ID)
                                    ON DELETE CASCADE
);

CREATE TABLE TAILOR (
                        USER_ID INT PRIMARY KEY,
                        Active_status BOOLEAN NOT NULL DEFAULT TRUE,
                        Specialty VARCHAR(100),
                        CONSTRAINT fk_tailor_user
                            FOREIGN KEY (USER_ID)
                                REFERENCES APP_USER(USER_ID)
                                ON DELETE CASCADE
);

CREATE TABLE CUSTOMER (
                          USER_ID INT PRIMARY KEY,
                          House_no VARCHAR(20),
                          Street VARCHAR(100),
                          City VARCHAR(50),
                          CONSTRAINT fk_customer_user
                              FOREIGN KEY (USER_ID)
                                  REFERENCES APP_USER(USER_ID)
                                  ON DELETE CASCADE
);

CREATE TABLE STYLE_TEMPLATE (
                                Style_ID INT AUTO_INCREMENT PRIMARY KEY,
                                Style_name VARCHAR(100) NOT NULL UNIQUE,
                                Base_Price DECIMAL(10,2) NOT NULL,
                                Estimated_Labor_Hours DECIMAL(5,2) NOT NULL,
                                CONSTRAINT chk_style_base_price
                                    CHECK (Base_Price >= 0),
                                CONSTRAINT chk_style_labor_hours
                                    CHECK (Estimated_Labor_Hours > 0)
);

CREATE TABLE STYLE_MEASUREMENT_REQUIREMENT (
                                               Style_ID INT NOT NULL,
                                               Measurement_Name VARCHAR(50) NOT NULL,
                                               PRIMARY KEY (Style_ID, Measurement_Name),
                                               CONSTRAINT fk_style_measurement_requirement
                                                   FOREIGN KEY (Style_ID)
                                                       REFERENCES STYLE_TEMPLATE(Style_ID)
                                                       ON DELETE CASCADE
);

CREATE TABLE INVENTORY_ITEM (
                                Item_ID INT AUTO_INCREMENT PRIMARY KEY,
                                Item_Name VARCHAR(100) NOT NULL UNIQUE,
                                Current_Stock_Qty INT NOT NULL,
                                Unit_Cost DECIMAL(10,2) NOT NULL,
                                Reorder_Level INT NOT NULL DEFAULT 0,
                                CONSTRAINT chk_inventory_stock
                                    CHECK (Current_Stock_Qty >= 0),
                                CONSTRAINT chk_inventory_cost
                                    CHECK (Unit_Cost >= 0),
                                CONSTRAINT chk_inventory_reorder
                                    CHECK (Reorder_Level >= 0)
);

CREATE TABLE OUTFIT_ORDER (
                              Order_ID INT AUTO_INCREMENT PRIMARY KEY,
                              Customer_ID INT NOT NULL,
                              Tailor_ID INT NOT NULL,
                              Style_ID INT NOT NULL,
                              Outfit_Type VARCHAR(50) NOT NULL,
                              Order_Date DATE NOT NULL,
                              Delivery_Date DATE NOT NULL,
                              Est_Labor_Hours DECIMAL(5,2) NOT NULL,
                              Order_Status ENUM(
        'MEASURED',
        'CUTTING',
        'SEWING',
        'READY_FOR_DELIVERY',
        'DELIVERED'
    ) NOT NULL DEFAULT 'MEASURED',
                              Total_Price DECIMAL(10,2) NOT NULL,
                              Advance_Paid DECIMAL(10,2) NOT NULL DEFAULT 0.00,

                              CONSTRAINT fk_order_customer
                                  FOREIGN KEY (Customer_ID)
                                      REFERENCES CUSTOMER(USER_ID),

                              CONSTRAINT fk_order_tailor
                                  FOREIGN KEY (Tailor_ID)
                                      REFERENCES TAILOR(USER_ID),

                              CONSTRAINT fk_order_style
                                  FOREIGN KEY (Style_ID)
                                      REFERENCES STYLE_TEMPLATE(Style_ID),

                              CONSTRAINT chk_order_dates
                                  CHECK (Delivery_Date >= Order_Date),

                              CONSTRAINT chk_order_labor
                                  CHECK (Est_Labor_Hours > 0),

                              CONSTRAINT chk_order_total
                                  CHECK (Total_Price >= 0),

                              CONSTRAINT chk_order_advance
                                  CHECK (Advance_Paid >= 0),

                              CONSTRAINT chk_order_advance_not_over_total
                                  CHECK (Advance_Paid <= Total_Price)
);

CREATE TABLE MEASUREMENT (
                             Measurement_ID INT AUTO_INCREMENT PRIMARY KEY,
                             Order_ID INT NOT NULL,
                             Body_Part VARCHAR(50) NOT NULL,
                             Inch_Value VARCHAR(20) NOT NULL,
                             CONSTRAINT fk_measurement_order
                                 FOREIGN KEY (Order_ID)
                                     REFERENCES OUTFIT_ORDER(Order_ID)
                                     ON DELETE CASCADE
);

CREATE TABLE FABRIC_MATERIAL (
                                 Fabric_ID INT AUTO_INCREMENT PRIMARY KEY,
                                 Order_ID INT NOT NULL,
                                 Material_Type VARCHAR(100) NOT NULL,
                                 Color VARCHAR(50) NOT NULL,
                                 Length_Meters DECIMAL(5,2) NOT NULL,
                                 CONSTRAINT fk_fabric_order
                                     FOREIGN KEY (Order_ID)
                                         REFERENCES OUTFIT_ORDER(Order_ID)
                                         ON DELETE CASCADE,
                                 CONSTRAINT chk_fabric_length
                                     CHECK (Length_Meters > 0)
);

CREATE TABLE ORDER_CONSUMES_INVENTORY (
                                          Order_ID INT NOT NULL,
                                          Item_ID INT NOT NULL,
                                          Quantity_Used INT NOT NULL,

                                          PRIMARY KEY (Order_ID, Item_ID),

                                          CONSTRAINT fk_consumes_order
                                              FOREIGN KEY (Order_ID)
                                                  REFERENCES OUTFIT_ORDER(Order_ID)
                                                  ON DELETE CASCADE,

                                          CONSTRAINT fk_consumes_item
                                              FOREIGN KEY (Item_ID)
                                                  REFERENCES INVENTORY_ITEM(Item_ID)
                                                  ON DELETE CASCADE,

                                          CONSTRAINT chk_quantity_used
                                              CHECK (Quantity_Used > 0)
);

CREATE TABLE TAILOR_ACTIVITY_LOG (
                                     Log_ID INT AUTO_INCREMENT PRIMARY KEY,
                                     Tailor_ID INT NOT NULL,
                                     Action_Type ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
                                     Target_Table VARCHAR(50) NOT NULL,
                                     Record_ID VARCHAR(50) NOT NULL,
                                     Old_Value TEXT,
                                     New_Value TEXT,
                                     Log_Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT fk_log_tailor
                                         FOREIGN KEY (Tailor_ID)
                                             REFERENCES APP_USER(USER_ID)
                                             ON DELETE CASCADE
);