# AsteroidsNWP
Este proyecto es una prueba desarrollada segun los criterios de Butterfly, contiene una pantalla donde se solicitan los permisos,
cuando estos son otorgados, se dirige al login donde se a travez de FirebaseAuth se puede o registrar un usuario o logearse con uno existente
el logeo contiene login offline y login online, si la persona realizo previamente login online, puede logearse con la misma informacion
aunque no tenga conexion a internet, al logearse exitosamente, vamos a la pantalla de asteroides donde se pueden consultar los asteroides cercanos a la tierra por fecha,
siemrpe que ya se haya consultado una fecha, se podra seguir visualizando aunque no haya conexion, asi como los detalles de cada asteroide, ya que toda la informacion
se almacena en SQLite en una base precargada con la estructura que se podra encontrar en assets y sigue disponible con o sin conexion, para el caso de prueba 
se implemento el funcionamiento de la logica a travez de lectura de la base de datos, al nivel de que aunque por una sobrecarga de la app los datos de cache se borrarran,
la app siempre regresa a la ultima pantalla  que el usuario estuviera consultando y mantiene el logeo offline y las fechas previamente consultadas.
