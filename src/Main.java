import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import udla.adminus.mmunoz.*;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static SQL util = new SQL();

    private static String cedulaActual = "";
    private static int tipoUsuarioActual = 0;
    private static boolean sesionIniciada = false;

    public static void main(String[] args) {
        while(true) {
            if(!sesionIniciada) {
                mostrarMenuInicial();
            } else {
                menuPrincipal();
            }
        }
    }

    // ==================== MENÚ INICIAL ====================

    private static void mostrarMenuInicial() {
        System.out.println("\n========================================");
        System.out.println("  SISTEMA DE GESTION UNIVERSITARIA");
        System.out.println("========================================");
        System.out.println("1. Iniciar Sesion");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opcion: ");

        int opcion = scanner.nextInt();
        scanner.nextLine();

        switch(opcion) {
            case 1:
                iniciarSesion();
                break;
            case 2:
                registrarse();
                break;
            case 3:
                System.out.println("\nGracias por usar el sistema. ¡Hasta pronto!");
                System.exit(0);
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    // ==================== REGISTRO ====================

    private static void registrarse() {
        System.out.println("\n=== REGISTRO DE USUARIO ===");
        System.out.println("Seleccione el tipo de usuario:");
        System.out.println("1. Estudiante");
        System.out.println("2. Docente");
        System.out.println("3. Personal Administrativo");
        System.out.print("Opcion: ");

        int tipoUsuario = scanner.nextInt();
        scanner.nextLine();

        System.out.println("\n--- DATOS PERSONALES ---");

        System.out.print("Cedula: ");
        String cedula = scanner.nextLine();
        if(cedula.isEmpty()) {
            System.out.println("ERROR: La cedula no puede estar vacia.");
            return;
        }

        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();
        if(nombre.isEmpty()) {
            System.out.println("ERROR: El nombre no puede estar vacio.");
            return;
        }

        int edad = obtenerEdadValida();

        System.out.print("Genero: ");
        String genero = scanner.nextLine();
        if(genero.isEmpty()) {
            System.out.println("ERROR: El genero no puede estar vacio.");
            return;
        }

        System.out.print("Direccion: ");
        String direccion = scanner.nextLine();
        if(direccion.isEmpty()) {
            System.out.println("ERROR: La direccion no puede estar vacia.");
            return;
        }

        long telefono = 0;
        boolean telefonoValido = false;
        while(!telefonoValido) {
            try {
                System.out.print("Telefono: ");
                telefono = scanner.nextLong();
                scanner.nextLine();
                telefonoValido = true;
            } catch(Exception e) {
                System.out.println("ERROR: Debe ingresar un numero valido.");
                scanner.nextLine();
            }
        }

        System.out.print("Email: ");
        String email = scanner.nextLine();
        if(email.isEmpty()) {
            System.out.println("ERROR: El email no puede estar vacio.");
            return;
        }

        if(tipoUsuario == 1) {
            registrarNuevoEstudiante(cedula, nombre, edad, genero, direccion, telefono, email);
        } else if(tipoUsuario == 2) {
            registrarNuevoDocente(cedula, nombre, edad, genero, direccion, telefono, email);
        } else if(tipoUsuario == 3) {
            registrarNuevoAdministrativo(cedula, nombre, edad, genero, direccion, telefono, email);
        } else {
            System.out.println("Tipo de usuario invalido.");
        }
    }

    private static void registrarNuevoEstudiante(String cedula, String nombre, int edad,
                                                 String genero, String direccion, long telefono, String email) {
        System.out.println("\n--- DATOS ACADEMICOS DEL ESTUDIANTE ---");

        System.out.print("Nivel (Ej: 1er Nivel, 2do Nivel): ");
        String nivel = scanner.nextLine();
        if(nivel.isEmpty()) {
            System.out.println("ERROR: El nivel no puede estar vacio.");
            return;
        }

        System.out.print("Paralelo (Ej: A, B, C): ");
        String paralelo = scanner.nextLine();
        if(paralelo.isEmpty()) {
            System.out.println("ERROR: El paralelo no puede estar vacio.");
            return;
        }

        System.out.print("Nombre del Representante Legal: ");
        String representante = scanner.nextLine();
        if(representante.isEmpty()) {
            System.out.println("ERROR: El nombre del representante no puede estar vacio.");
            return;
        }

        System.out.print("Periodo Academico (Ej: 2024-1, 2024-2): ");
        String periodoAcademico = scanner.nextLine();
        if(periodoAcademico.isEmpty()) {
            System.out.println("ERROR: El periodo academico no puede estar vacio.");
            return;
        }

        Estudiante estudiante = new Estudiante(cedula, nombre, edad, genero, direccion,
                telefono, email, nivel, paralelo, representante, periodoAcademico);

        Connection conn = util.getConnection();
        if (conn != null) {
            System.out.println("¡¡Conectados!!");
        } else {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        util.insertarDatos(estudiante, conn);

        System.out.println("\n========================================");
        System.out.println("Estudiante registrado exitosamente!");
        System.out.println("========================================");
        System.out.println(estudiante.mostrarInformacion());
        System.out.println("\nNOTA: Las inscripciones en asignaturas las realizan los docentes.");
    }

    private static void registrarNuevoDocente(String cedula, String nombre, int edad,
                                              String genero, String direccion, long telefono, String email) {
        System.out.println("\n--- DATOS LABORALES DEL DOCENTE ---");

        double sueldoMensual = obtenerSueldoValido();

        System.out.print("Jornada Laboral: ");
        String jornada = scanner.nextLine();
        if(jornada.isEmpty()) {
            System.out.println("ERROR: La jornada laboral no puede estar vacia.");
            return;
        }

        int horas = obtenerHorasTrabajadasValidas();

        System.out.print("Especialidad: ");
        String especialidad = scanner.nextLine();
        if(especialidad.isEmpty()) {
            System.out.println("ERROR: La especialidad no puede estar vacia.");
            return;
        }

        System.out.print("Titulo Academico: ");
        String titulo = scanner.nextLine();
        if(titulo.isEmpty()) {
            System.out.println("ERROR: El titulo academico no puede estar vacio.");
            return;
        }

        System.out.print("Carga Horaria: ");
        int carga = obtenerHorasTrabajadasValidas();

        System.out.print("Horario de Clases (Ej: Lunes-Viernes 8:00-12:00): ");
        String horario_clases = scanner.nextLine();
        if(horario_clases.isEmpty()) {
            System.out.println("ERROR: El horario no puede estar vacio.");
            return;
        }

        System.out.println("\n--- DATOS DE LA ASIGNATURA QUE IMPARTIRA ---");

        String nombreAsignatura = "";
        while(nombreAsignatura.isEmpty()) {
            System.out.print("Nombre de la Asignatura: ");
            nombreAsignatura = scanner.nextLine().trim();
            if(nombreAsignatura.isEmpty()) {
                System.out.println("ERROR: El nombre de la asignatura no puede estar vacio.");
            }
        }

        String codigoAsignatura = "";
        while(codigoAsignatura.isEmpty()) {
            System.out.print("Codigo de la Asignatura: ");
            codigoAsignatura = scanner.nextLine().trim();
            if(codigoAsignatura.isEmpty()) {
                System.out.println("ERROR: El codigo de la asignatura no puede estar vacio.");
            }
        }

        int horasSemanales = 0;
        boolean horasValidas = false;
        while(!horasValidas) {
            try {
                System.out.print("Horas Semanales: ");
                horasSemanales = scanner.nextInt();
                scanner.nextLine();
                if(horasSemanales > 0) {
                    horasValidas = true;
                } else {
                    System.out.println("ERROR: Las horas semanales deben ser mayores a 0.");
                }
            } catch(Exception e) {
                System.out.println("ERROR: Debe ingresar un numero valido.");
                scanner.nextLine();
            }
        }

        Connection conn = util.getConnection();
        if (conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        System.out.println("¡¡Conectados!!");

        int idAsignatura = util.registrarAsignatura(nombreAsignatura, codigoAsignatura, horasSemanales, conn);

        if(idAsignatura == -1) {
            System.out.println("ERROR: No se pudo registrar la asignatura.");
            return;
        }

        System.out.println("\nAsignatura registrada exitosamente: " + nombreAsignatura + " (" + codigoAsignatura + ")");

        Docente docente = new Docente(cedula, nombre, edad, genero, direccion, telefono,
                email, sueldoMensual, jornada, horas, especialidad, titulo, carga);

        util.insertarDocente(docente, conn, horario_clases, idAsignatura);

        System.out.println("\nAsignatura asignada: " + nombreAsignatura + " (" + codigoAsignatura + ")");
        System.out.println(docente.mostrarInformacion());
    }

    private static void registrarNuevoAdministrativo(String cedula, String nombre, int edad,
                                                     String genero, String direccion, long telefono, String email) {
        System.out.println("\n--- DATOS LABORALES DEL PERSONAL ADMINISTRATIVO ---");

        System.out.print("Cargo (Ej: Secretario, Coordinador): ");
        String cargo = scanner.nextLine();
        if(cargo.isEmpty()) {
            System.out.println("ERROR: El cargo no puede estar vacio.");
            return;
        }

        System.out.print("Area (Ej: Administrativa, Academica): ");
        String area = scanner.nextLine();
        if(area.isEmpty()) {
            System.out.println("ERROR: El area no puede estar vacia.");
            return;
        }

        System.out.print("Jornada Laboral: ");
        String jornadaLaboral = scanner.nextLine();
        if(jornadaLaboral.isEmpty()) {
            System.out.println("ERROR: La jornada laboral no puede estar vacia.");
            return;
        }

        int horasTrabajadas = obtenerHorasTrabajadasValidas();
        double sueldo = obtenerSueldoValido();

        Connection conn = util.getConnection();
        if (conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        System.out.println("¡¡Conectados!!");

        Administrativo administrativo = new Administrativo(cedula, nombre, edad, genero, direccion,
                telefono, email, cargo, area, jornadaLaboral, horasTrabajadas, sueldo);

        util.insertarAdministrativo(administrativo, conn);

        System.out.println("\n========================================");
        System.out.println("Personal Administrativo registrado exitosamente!");
        System.out.println("========================================");
        System.out.println(administrativo.mostrarInformacion());
    }

    // ==================== INICIO DE SESIÓN ====================

    private static void iniciarSesion() {
        System.out.println("\n=== INICIAR SESION ===");
        System.out.print("Cedula: ");
        String cedula = scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        if(!util.verificarUsuarioExiste(cedula, conn)) {
            System.out.println("\nERROR: Usuario no encontrado. Por favor registrese primero.");
            return;
        }

        int tipoUsuario = util.verificarUsuario(cedula, conn);

        if(tipoUsuario == 0) {
            System.out.println("\nERROR: No se pudo determinar el tipo de usuario.");
            return;
        }

        cedulaActual = cedula;
        tipoUsuarioActual = tipoUsuario;
        sesionIniciada = true;

        String tipoNombre = tipoUsuario == 1 ? "Estudiante" : (tipoUsuario == 2 ? "Docente" : "Personal Administrativo");
        System.out.println("\n¡Bienvenido! Ha iniciado sesion como: " + tipoNombre);
    }

    // ==================== MENÚ PRINCIPAL ====================

    private static void menuPrincipal() {
        if(tipoUsuarioActual == 1) {
            mostrarMenuEstudiante();
            int opcion = scanner.nextInt();
            scanner.nextLine();
            menuEstudiante(opcion);
        } else if(tipoUsuarioActual == 2) {
            mostrarMenuDocente();
            int opcion = scanner.nextInt();
            scanner.nextLine();
            menuDocente(opcion);
        } else if(tipoUsuarioActual == 3) {
            mostrarMenuAdministrativo();
            int opcion = scanner.nextInt();
            scanner.nextLine();
            menuAdministrativo(opcion);
        }
    }

    // ==================== MENÚS DE CADA TIPO DE USUARIO ====================

    private static void mostrarMenuEstudiante() {
        System.out.println("\n========================================");
        System.out.println("  MENU ESTUDIANTE");
        System.out.println("========================================");
        System.out.println("1. Ver Notas");
        System.out.println("2. Ver Asistencia");
        System.out.println("3. Ver mi Informacion");
        System.out.println("4. Cerrar Sesion");
        System.out.print("Seleccione una opcion: ");
    }

    private static void mostrarMenuDocente() {
        System.out.println("\n========================================");
        System.out.println("  MENU DOCENTE");
        System.out.println("========================================");
        System.out.println("1. Gestionar Notas de Estudiantes");
        System.out.println("2. Gestionar Asistencia de Estudiantes");
        System.out.println("3. Inscribir Estudiante en mi Materia");
        System.out.println("4. Ver Estudiantes Inscritos");
        System.out.println("5. Ver mi Informacion");
        System.out.println("6. Cerrar Sesion");
        System.out.print("Seleccione una opcion: ");
    }

    private static void mostrarMenuAdministrativo() {
        System.out.println("\n========================================");
        System.out.println("  MENU PERSONAL ADMINISTRATIVO");
        System.out.println("========================================");
        System.out.println("1. Ver mi Informacion");
        System.out.println("2. Cambiar Estado de Empleabilidad");
        System.out.println("3. Cerrar Sesion");
        System.out.print("Seleccione una opcion: ");
    }

    // ==================== LÓGICA DE MENÚS ====================

    private static void menuEstudiante(int opcion) {
        Connection conn = util.getConnection();
        switch(opcion) {
            case 1:
                System.out.print("\nIngrese el ID de la asignatura: ");
                int idAsignatura = scanner.nextInt();
                scanner.nextLine();
                util.verNotasEstudiante(cedulaActual, idAsignatura, conn);
                break;
            case 2:
                System.out.print("\nIngrese el ID de la asignatura: ");
                int idAsignaturaAsistencia = scanner.nextInt();
                scanner.nextLine();
                util.verAsistenciaEstudiante(cedulaActual, idAsignaturaAsistencia, conn);
                break;
            case 3:
                util.mostrarInformacionPersona(cedulaActual, conn);
                break;
            case 4:
                cerrarSesion();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    private static void menuDocente(int opcion) {
        switch(opcion) {
            case 1:
                gestionarNotasEstudiantes();
                break;
            case 2:
                gestionarAsistencias();
                break;
            case 3:
                inscribirEstudianteEnMateria();
                break;
            case 4:
                verEstudiantesInscritos();
                break;
            case 5:
                verInformacionDocente();
                break;
            case 6:
                cerrarSesion();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    private static void menuAdministrativo(int opcion) {
        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        switch(opcion) {
            case 1:
                util.mostrarInformacionPersona(cedulaActual, conn);
                break;
            case 2:
                System.out.println("\n=== CAMBIAR ESTADO DE EMPLEABILIDAD ===");
                System.out.print("Ingrese la cedula del empleado: ");
                String cedulaEmpleado = scanner.nextLine();

                System.out.print("Ingrese el nuevo estado (Activo/Inactivo/Licencia): ");
                String nuevoEstado = scanner.nextLine();

                util.cambiarEstadoEmpleabilidad(cedulaEmpleado, nuevoEstado, conn);
                break;
            case 3:
                cerrarSesion();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    // ==================== FUNCIONES DEL DOCENTE ====================

    private static void gestionarNotasEstudiantes() {
        System.out.println("\n=== GESTION DE NOTAS ===");
        System.out.println("1. Ingresar notas de estudiantes");
        System.out.println("2. Ver notas de estudiantes");
        System.out.println("3. Editar notas de estudiantes");
        System.out.print("Seleccione una opcion: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        if(opcion == 1) {
            System.out.print("Cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            System.out.print("ID de la asignatura: ");
            int idAsignatura = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Parcial (Primer Parcial, Segundo Parcial, Tercer Parcial): ");
            String parcial = scanner.nextLine();
            System.out.print("Nota: ");
            double nota = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Fecha de registro (YYYY-MM-DD): ");
            String fecha = scanner.nextLine();

            util.insertarNota(cedulaEstudiante, idAsignatura, parcial, nota, fecha, conn);
        } else if(opcion == 2) {
            System.out.print("Cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            System.out.print("ID de la asignatura: ");
            int idAsignatura = scanner.nextInt();
            scanner.nextLine();

            util.verNotasEstudiante(cedulaEstudiante, idAsignatura, conn);
        } else if(opcion == 3) {
            System.out.print("Cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            System.out.print("ID de la asignatura: ");
            int idAsignatura = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Parcial a editar: ");
            String parcial = scanner.nextLine();
            System.out.print("Nueva nota: ");
            double nota = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Fecha de actualizacion (YYYY-MM-DD): ");
            String fecha = scanner.nextLine();

            util.actualizarNota(cedulaEstudiante, idAsignatura, parcial, nota, fecha, conn);
        }
    }

    private static void gestionarAsistencias() {
        System.out.println("\n=== GESTION DE ASISTENCIAS ===");
        System.out.println("1. Registrar asistencia");
        System.out.println("2. Ver asistencias");
        System.out.println("3. Editar asistencia");
        System.out.print("Seleccione una opcion: ");
        int opcion = scanner.nextInt();
        scanner.nextLine();

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        if(opcion == 1) {
            System.out.print("Cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            System.out.print("ID de la asignatura: ");
            int idAsignatura = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Fecha (YYYY-MM-DD): ");
            String fecha = scanner.nextLine();
            System.out.print("Estado (Presente/Ausente/Tardanza): ");
            String estado = scanner.nextLine();

            util.registrarAsistencia(cedulaEstudiante, idAsignatura, fecha, estado, conn);
        } else if(opcion == 2) {
            System.out.print("Cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            System.out.print("ID de la asignatura: ");
            int idAsignatura = scanner.nextInt();
            scanner.nextLine();

            util.verAsistenciaEstudiante(cedulaEstudiante, idAsignatura, conn);
        } else if(opcion == 3) {
            System.out.print("Cedula del estudiante: ");
            String cedulaEstudiante = scanner.nextLine();
            System.out.print("ID de la asignatura: ");
            int idAsignatura = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Fecha a editar (YYYY-MM-DD): ");
            String fecha = scanner.nextLine();
            System.out.print("Nuevo estado: ");
            String estado = scanner.nextLine();

            util.actualizarAsistencia(cedulaEstudiante, idAsignatura, fecha, estado, conn);
        }
    }

    private static void inscribirEstudianteEnMateria() {
        System.out.println("\n=== INSCRIBIR ESTUDIANTE EN MI MATERIA ===");

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        int idAsignatura = obtenerMateriaDocente(cedulaActual, conn);
        if(idAsignatura == -1) {
            System.out.println("ERROR: No se pudo obtener su asignatura.");
            return;
        }

        System.out.println("\n--- ESTUDIANTES DISPONIBLES ---");
        util.mostrarTodosLosEstudiantes(conn);

        System.out.print("\nIngrese la cedula del estudiante a inscribir: ");
        String cedulaEstudiante = scanner.nextLine();

        if(!verificarEstudianteExiste(cedulaEstudiante, conn)) {
            System.out.println("ERROR: El estudiante no existe.");
            return;
        }

        inscribirEstudiante(cedulaEstudiante, idAsignatura, conn);
    }

    private static void verEstudiantesInscritos() {
        System.out.println("\n=== ESTUDIANTES INSCRITOS EN MI MATERIA ===");

        Connection conn = util.getConnection();
        if(conn == null) {
            System.out.println("ERROR: No se pudo conectar a la base de datos.");
            return;
        }

        int idAsignatura = obtenerMateriaDocente(cedulaActual, conn);
        if(idAsignatura == -1) {
            System.out.println("ERROR: No se pudo obtener su asignatura.");
            return;
        }

        mostrarMateriaDocente(idAsignatura, conn);
        mostrarEstudiantesInscritos(idAsignatura, conn);
    }

    private static void verInformacionDocente() {
        Connection conn = util.getConnection();
        if(conn != null) {
            util.mostrarInformacionPersona(cedulaActual, conn);
        }
    }

    // ==================== FUNCIONES AUXILIARES ====================

    private static int obtenerMateriaDocente(String cedulaDocente, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT asignatura_id FROM Docente WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaDocente);
            rs = ps.executeQuery();

            if(rs.next()) {
                return rs.getInt("asignatura_id");
            }

        } catch(SQLException ex) {
            System.out.println("Error al obtener materia del docente:");
            ex.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(ps != null) ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        return -1;
    }

    private static void mostrarMateriaDocente(int idMateria, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT nombre, codigo FROM Asignatura WHERE id_asignatura = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idMateria);
            rs = ps.executeQuery();

            if(rs.next()) {
                System.out.println("\nAsignatura: " + rs.getString("nombre") + " (" + rs.getString("codigo") + ")");
            }

        } catch(SQLException ex) {
            System.out.println("Error al mostrar materia:");
            ex.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(ps != null) ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void inscribirEstudiante(String cedulaEstudiante, int idAsignatura, Connection conn) {
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO Inscripcion (cedula_estudiante, id_asignatura, fecha_inscripcion) VALUES (?, ?, CURRENT_TIMESTAMP)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, idAsignatura);
            ps.executeUpdate();

            System.out.println("\nEstudiante inscrito exitosamente!");

        } catch(SQLException ex) {
            if(ex.getErrorCode() == 1062) {
                System.out.println("ERROR: El estudiante ya esta inscrito en esta asignatura.");
            } else {
                System.out.println("Error al inscribir estudiante:");
                ex.printStackTrace();
            }
        } finally {
            try {
                if(ps != null) ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void mostrarEstudiantesInscritos(int idAsignatura, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT p.cedula, p.nombre, e.nivel, e.paralelo " +
                        "FROM Persona p " +
                        "INNER JOIN Estudiante e ON p.cedula = e.cedula " +
                        "INNER JOIN Inscripcion i ON e.cedula = i.cedula_estudiante " +
                        "WHERE i.id_asignatura = ? " +
                        "ORDER BY p.nombre";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idAsignatura);
            rs = ps.executeQuery();

            System.out.println("\n========================================");
            System.out.println("  ESTUDIANTES INSCRITOS");
            System.out.println("========================================");
            System.out.printf("%-15s %-30s %-10s %-10s%n", "CEDULA", "NOMBRE", "NIVEL", "PARALELO");
            System.out.println("----------------------------------------");

            boolean hayEstudiantes = false;
            while(rs.next()) {
                hayEstudiantes = true;
                System.out.printf("%-15s %-30s %-10s %-10s%n",
                        rs.getString("cedula"),
                        rs.getString("nombre"),
                        rs.getString("nivel"),
                        rs.getString("paralelo"));
            }

            if(!hayEstudiantes) {
                System.out.println("No hay estudiantes inscritos.");
            }

            System.out.println("========================================\n");

        } catch(SQLException ex) {
            System.out.println("Error al listar estudiantes:");
            ex.printStackTrace();
        } finally {
            try {
                if(rs != null) rs.close();
                if(ps != null) ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void cerrarSesion() {
        System.out.println("\nCerrando sesion...");
        cedulaActual = "";
        tipoUsuarioActual = 0;
        sesionIniciada = false;
        System.out.println("Sesion cerrada exitosamente.");
    }

    private static int obtenerEdadValida() {
        int edad = 0;
        boolean edadValida = false;

        while(!edadValida) {
            try {
                System.out.print("Edad: ");
                edad = scanner.nextInt();
                scanner.nextLine();
                if(edad > 0 && edad < 120) {
                    edadValida = true;
                } else {
                    System.out.println("ERROR: Ingrese una edad valida.");
                }
            } catch(Exception e) {
                System.out.println("ERROR: Debe ingresar un numero.");
                scanner.nextLine();
            }
        }

        return edad;
    }

    private static double obtenerSueldoValido() {
        double sueldo = 0.0;
        boolean sueldoValido = false;

        while(!sueldoValido) {
            try {
                System.out.print("Sueldo Mensual: ");
                sueldo = scanner.nextDouble();
                scanner.nextLine();
                if(sueldo > 0) {
                    sueldoValido = true;
                } else {
                    System.out.println("ERROR: El sueldo debe ser mayor a 0.");
                }
            } catch(Exception e) {
                System.out.println("ERROR: Debe ingresar un numero valido.");
                scanner.nextLine();
            }
        }

        return sueldo;
    }

    private static int obtenerHorasTrabajadasValidas() {
        int horas = 0;
        boolean horasValidas = false;

        while(!horasValidas) {
            try {
                System.out.print("Horas Trabajadas: ");
                horas = scanner.nextInt();
                scanner.nextLine();
                if(horas > 0) {
                    horasValidas = true;
                } else {
                    System.out.println("ERROR: Las horas deben ser mayores a 0.");
                }
            } catch(Exception e) {
                System.out.println("ERROR: Debe ingresar un numero valido.");
                scanner.nextLine();
            }
        }

        return horas;
    }

    private static boolean verificarEstudianteExiste(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT cedula FROM Estudiante WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            return rs.next();

        } catch(SQLException ex) {
            System.out.println("Error al verificar estudiante:");
            ex.printStackTrace();
            return false;
        } finally {
            try {
                if(rs != null) rs.close();
                if(ps != null) ps.close();
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }
    }
}