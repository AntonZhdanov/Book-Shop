<div align="center">
    <h1> My Awesome Online Book Store Project <img src="https://drive.google.com/uc?export=view&id=1OGa8hOBQlA6EM1fGVNJVrK_ocf36DiAO" align="center" width="60" /></h1>
</div>



<div align="center">
    <a href="#introduction">Introduction</a> |
    <a href="#technologies">Technologies & Tools</a> |
    <a href="#functionalities">Functionalities</a> |
    <a href="#setup">Setup</a> |
    <a href="#video instruction">Short Video Instruction</a> |
    <a href="#usage_postman and swagger">Usage tools for testing</a> |
    <a href="#contribute">Contribute</a>
</div>


<div id="introduction">
    <br>
    <p>
        Welcome to my awesome Bookstore. This application was created for book lovers 
        who want to choose the right book, search the library and add everything necessary to create an order.
        This project incorporates a variety of domain models including User, Role, Book, Category, 
        ShoppingCart, CartItem, Order, and OrderItem. Users have the ability to register, log in, explore books, 
        add selections to their shopping cart, and finalize orders. Conversely, administrators have the capabilities 
        to oversee books, manage bookshelf sections, and observe and modify the statuses of orders.
This README file will describe how to work with the application and the steps to run it on your machine.
    </p>
</div>
<hr>

<div id="technologies">
    <h3> Technologies & Tools </h3>
    <p>
        This web application is built on SpringBoot using a plethora of modern technologies and tools to ensure 
        robustness and scalability, including but not limited to:
       <br> - Spring Boot Framework for the backend 
       <br> - Spring Security for authentication and security
       <br> - Spring Data JPA for database interaction
       <br> - Swagger for API documentation
       <br> - Maven for connect libraries and build the project
       <br> - IntelliJ IDEA recommended development enviorment
       <br> - Docker for running in an isolated environment on different platforms
       <br> - Liquibase for managing database schema changes and tracking revisions 
       <br> - Spring Testing for conducting tests to ensure the application's correctness and stability
</p>
</div>
<hr>

<div id="functionalities">
    <h3> Functionalities </h3>
    <p>
        In this project, two user roles ROLE_USER by default and ROLE_ADMIN
        are implemented, they have the following capabilities:
        <br> Book buyers(users) can:
        <br>- Sign up and log in to gain access to the store.
        <br>- Explore a wide-ranging collection of books.
        <br>- Effortlessly search for particular books.
        <br>- Place books in their shopping cart.
        <br>- Examine and organize the items in their shopping cart.
        <br>- Finalize orders and observe their order history.
        <br> Manager(Admin) can:
        <br>- Effectively oversee the store's book inventory.
        <br>- Create, modify, and retrieve information about all the books in the store.
        <br>- Track and modify order statuses to enhance order management processes.
</p>
</div>
<hr>

<div id="setup">
    <h3> Setup </h3>
    <p>
        Getting started with this project is easy. Follow the steps below to set up the 
        project on your local machine:
        <br> Pre required:<br>
        <br> Before getting started, make sure you fulfill the following requirements:
        <br> 0. Install Postman(for make requests to endpoints or using web browser);
        <br> 1. Installed JDK and IntelliJ IDEA;
        <br> 2. MySql/PostgresSql or another preferable relational database;
        <br> 3. Maven (for building the project);
        <br> 4. Docker (for running project in virtual container);<br>
        <br>Running for your local machine:<br>
        <br> 1. Clone this repository: `git clone https://github.com/AntonZhdanov/Book-Store`.
        <br> 2. You need to configure application.properties file to connect the database before running the app.
        <br> 3. Build the project: `mvn clean install`.
        <br> 4. Run the app: `mvn spring-boot:run`.<br>
        <br>Running with Docker in your machine:<br>
        <br> 0. Install Docker Desktop(Optional): `https://www.docker.com/products/docker-desktop/`.
        <br> 1. Run command(for running docker image):docker-compose build.
        <br> 2. Run the docker container: docker-compose up.
        <br> 3. If you need to stop them(containers): docker-compose down.<br>
        <br> While application is running, you can access the Swagger UI for 
         API documentation and testing(Optional): 
        <br> 1. Swagger UI URL: http://localhost:8080/api/swagger-ui/index.html
</p>
</div>
<hr>

<div id="video instruction">
    <h3> Short Video Instruction </h3>
    <p>
        Additionally, you can view a brief video to familiarize yourself with the workflow:
        <br> - Here is the link: `https://www.loom.com/share/51f94f2fdfeb4187b9365b231f9de875.`
    </p>
</div>
<hr>

<div id="usage_postman and swagger">
    <h3> Usage tools for testing </h3>
    <p>
        This project offers an intuitive interface and detailed API documentation via Swagger, 
        which can be accessed at [http://localhost:8080/api/swagger-ui/index.html]. Additionally, a collection of Postman requests 
        can be found [http://surl.li/mlndt], in order to make requests to API endpoints.
    </p>
</div>
<hr>

<div id="contribute">
    <h3> Contribute </h3>
    <p>
        Contributions are very welcome! If you wish to contribute or have any questions, 
        please contact me through LinkedIn(http://surl.li/louut).
    </p>
</div>
<hr>