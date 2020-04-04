# Project Future Delivery
## Description
Our project is an automation of transport logistics tasks between warehouses and consumers. The main goal of this solution is to automate and optimize the construction of transport routes. Optimization is based on the location of transport stops and the amount of cargo carried.
## Implemented functions
- authorization and registration of each participating person (using JWT). There are 4 such persons (administrator, warehouse manager, store manager, driver). Validation is also provided for warehouses and stores;
- distances to each existing point are filled asynchronously, using the Google Map API. This is done every time when a new warehouse or store is registered;
- getting a list of goods available for ordering;
- receiving a list of crated orders, indicating their status;
- order creation from a list of available products;
- removal (cancellation) of the order before it was distributed by calculation algorithms;

- getting a list of available product items in stock;
- editing the number of items;
- adding a new trade name;

- driver can receive the list of routes he is assigned to;
- driver can note the delivery of goods;

- daily asynchronous schedule (at midnight) building of routes using 4 computational algorithms:

  - transportation problem: calculate optimal volumes of transportation and participating points for each product;
  - wiper algorithm: distribute traffic among cars. Calculate number of delivery points (stores) for 1 route;
  - vehicle routing problem algorithm: calculate the optimal order of the delivery points in the route sheet;
  - another simple calculations, based on the results of the 3 above-stated algorithms: calculate the cost of delivery from the calculation of the consumption of the delivery vehicle.
  
- many custom exceptions were created for returning a correct HTTP error code and message from the API;
- Google Maps API was used to calculate distances between addresses using coordinates or addresses in a natural (human-readable) form.

## Tech stack
- Spring IoC
- Spring Web
- Spring Security
- Spring Schedule
- Hibernate
- Spring Data
- JWT
- Google Maps API

## Requirements:
### Configuration file (resources/application.yml) which contain:
- server port
- JWT secret ```spring:security:jwt:token:JWT_SECRET```
- correct data source parameters ```Postgresql preferably```
- [Google Maps Distance Matrix API key](https://developers.google.com/maps/documentation/distance-matrix/intro) ```third-party:google:api-key```

### API Reference by Swagger:
After start main project, swagger UI available on: ```http://<host>:<port>/swagger-ui.html``` all api endpoints are documented.
#### Test data generation
Test data generator located in [src/test/java/com/nix/futuredelivery/DatabaseRandomDataFiller.java](https://github.com/riregot2545/FutureDelivery/blob/master/src/test/java/com/nix/futuredelivery/DatabaseRandomDataFiller.java)
  
You can run ```randomSmallDataTest```  method to generate light version of test data, or ```randomMediumDataTest ``` to generate normal variant. Main difference between two variants is count of stores and their orders. So it affect on time of route building. 
