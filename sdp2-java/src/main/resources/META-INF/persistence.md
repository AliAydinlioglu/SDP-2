<?xml version="1.0" encoding="UTF-8"?>

<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
                                 http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">
<persistence-unit name="sdp2" transaction-type="RESOURCE_LOCAL">
<provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>

        <!-- Entity classes -->
        <class>domain.Gebruiker</class>
        <class>domain.Machine</class>
        <class>domain.Site</class>

        <properties>
            <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/sdp2?serverTimezone=UTC"/>
            <property name="jakarta.persistence.jdbc.user" value="HERE_COMES_YOUR_USERNAME"/>
            <property name="jakarta.persistence.jdbc.password" value="HERE_COMES_YOUR_PASSWORD"/>
            <property name="jakarta.persistence.jdbc.driver" value="com.mysql.cj.jdbc.Driver"/>

            <!-- Schema generation - this enables automatic table creation -->
            <property name="jakarta.persistence.schema-generation.database.action" value="create"/>
        </properties>
    </persistence-unit>

</persistence>
