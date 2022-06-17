![AHORCADO](https://learn.g2.com/hubfs/G2CM_FI044_Learn_Article_Images-Employee_Turnover_V1b.png)

# 👩‍💻 API-de-Turnos-Rotativos
 Una API para el manejo de turnos rotativos
 - Link del Proyecto: [github.com/FacuCR/API-de-Turnos-Rotativos](https://github.com/FacuCR/API-de-Turnos-Rotativos)

-----
## 💻 Lenguajes
![JAVA](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![SPRING](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

-----
## 💡 Descripción
Se necesita crear una API para el manejo de turnos rotativos utilizando Spring Boot, Maven y MySQL
(excluyente); que permita a los usuarios gestionar las horas de trabajo y su jornada laboral.
Esta API deberá permitir:

-----
## ✔️ Requisitos:
- Dar de alta un empleado.
- Dar de alta el tipo de jornada laboral (Turno Extra, Vacaciones, Día Libre, Turno Normal,
etc.).
- Cargar la jornada laboral de un empleado, especificando el tipo, la fecha, hora de entrada y
salida.
- Listar para cada empleado la cantidad de horas cargadas por cada tipo de jornada laboral.
- Permitir modificar la cantidad de horas de una jornada laboral previamente cargada.
### Tener en consideración que:
- Cada empleado no puede trabajar más de 48 horas semanales, ni menos de 30.
- Las horas de un turno normal pueden variar entre 6 y 8, y de un turno extra entre 2 y 6.
- Para cada fecha, un empleado (siempre que no esté de vacaciones o haya pedido día libre)
podrá cargar un turno normal, un turno extra o una combinación de ambos que no supere
las 12 horas.
- Por cada turno no puede haber más que 2 empleados.
- Si un empleado cargó “Dia libre” no podrá trabajar durante esas 24 horas.
- En la semana el empleado podrá tener hasta 2 días libres.

-----

## 🧐 Observaciones
Como nos dijeron que todo suma, decidi implementar spring security con JWT y roles.
### 🤜 Sobre los roles
Hay dos roles user y admin, los user
serian los empleados que solo podrian tener acceso a modificar sus jornadas laborales, los admin por otra parte
tienen los mismos permisos que los user pero ademas son los unicos habilitados para crear otras cuentas. Tome esa 
decisión ya que en el contexto del tp entendi que era una empresa y no deberia poder venir cualquiera y crearse una
cuenta sin ser empleado de esa empresa, es por eso que solo los admins estan habilitados para crear
cuentas ademas del beneficio de que cada usuario(osea empleado) va ingresar en su cuenta y manejar su propia jornada ya que el usuario estara vinculado a su jornada
### 🔌 Inicializacion de la base de datos
En resources/data.sql se encuentra el archivo donde se inicializan los datos basicos
necesarios para poder usar la api sin problemas, los cuales son insertar los roles(user y admin) y una cuenta admin
ya que no se podria crear una cuenta sin autorizacion admin(habria que hacerlo manualmente)
### ⏱️ La decisión de hacer que el turno de un día sea de forma enum
Como dos personas podian tener un mismo turno y al no saber con que criterio comparar un turno por ej alguien entra
a las 8 am y sale a las 12pm comparada con otra que entra a las 7am y sale a las 11am, como sabria si es el mismo turno?
por eso tome la desicion de envez de crear a las clases de turno con una hora de entrada y una de salida y tener que 
estar comparando tantas horas decidi crear un atributo de tipo enum llamada ETurno que pueden ser TURNO_MANIANA, 
TURNO_TARDE y TURNO_NOCHE de esta manera
a la hora de comparar turnos es mucho mas facial(de la forma en la que yo lo pense obviamente) una hora de entrada o 
salida seria reemplazado por un atributo de tipo int con cantidad de horas, osea en resumen, alguien podria trabajar
en el TURNO_TARDE una cantidad de 4hs
### 👉👈 Aclaracion sobre la tabla jornadas
Se que en la forma en la que yo lo plantee jornada laboral esta de mas, lamentablemente me di cuenta casi al final y 
me daba miedo cambiar a ultimo momento y borrar la tabla y conectar las jornadas(turnos, dias libres y vacaciones) 
directamente al usuario y que despues explote todo por los aires(como diria dante el elefante)
por otra parte la antiguedad si hiciera esto iria en la tabla empleado, se lo puse a la tabla jornada para que no se 
sienta tan inutil :(

----

## 🛠️ Ejemplos de usos con Postman

1. Inicio sesion con el admin![login](https://user-images.githubusercontent.com/48571169/174406936-08ba18d0-4258-4f7e-aee8-b0b0d6a7edf3.jpg)

2. Creo un usuario al cual se le asigna una jornada![signup](https://user-images.githubusercontent.com/48571169/174406941-7cfef23b-33cf-4e8f-a773-1247e7c78e00.jpg)

3. Uso esa jornada para guardarle un turno![crearTurno](https://user-images.githubusercontent.com/48571169/174406950-d81a48ef-fc92-4fed-9fe6-1c1eba3c5dc9.jpg)
