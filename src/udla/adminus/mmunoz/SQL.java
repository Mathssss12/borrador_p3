package udla.adminus.mmunoz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQL {

    // ==================== CONEXIÓN ====================

    public Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/adminus";
        String user = "root";
        String passwd = "sasa";

        try {
            return DriverManager.getConnection(url, user, passwd);
        } catch (SQLException ex) {
            System.out.println("Error al conectar con la base de datos:");
            ex.printStackTrace();
        }
        return null;
    }

    // ==================== VERIFICACIONES DE USUARIO ====================

    public boolean verificarUsuarioExiste(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT cedula FROM Usuario WHERE cedula = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            return rs.next();

        } catch (SQLException ex) {
            System.out.println("Error al verificar usuario:");
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int verificarTipoUsuario(String cedula, Connection conn) {
        PreparedStatement psEstudiante = null;
        PreparedStatement psDocente = null;
        PreparedStatement psAdmin = null;
        ResultSet rsEstudiante = null;
        ResultSet rsDocente = null;
        ResultSet rsAdmin = null;

        try {
            String sqlEstudiante = "SELECT cedula FROM Estudiante WHERE cedula = ?";
            psEstudiante = conn.prepareStatement(sqlEstudiante);
            psEstudiante.setString(1, cedula);
            rsEstudiante = psEstudiante.executeQuery();

            if (rsEstudiante.next()) {
                return 1;
            }

            String sqlDocente = "SELECT cedula FROM Docente WHERE cedula = ?";
            psDocente = conn.prepareStatement(sqlDocente);
            psDocente.setString(1, cedula);
            rsDocente = psDocente.executeQuery();

            if (rsDocente.next()) {
                return 2;
            }

            String sqlAdmin = "SELECT cedula FROM Administrativo WHERE cedula = ?";
            psAdmin = conn.prepareStatement(sqlAdmin);
            psAdmin.setString(1, cedula);
            rsAdmin = psAdmin.executeQuery();

            if (rsAdmin.next()) {
                return 3;
            }

        } catch (SQLException ex) {
            System.out.println("Error al verificar tipo de usuario:");
            ex.printStackTrace();
        } finally {
            try {
                if (rsEstudiante != null) rsEstudiante.close();
                if (rsDocente != null) rsDocente.close();
                if (rsAdmin != null) rsAdmin.close();
                if (psEstudiante != null) psEstudiante.close();
                if (psDocente != null) psDocente.close();
                if (psAdmin != null) psAdmin.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public int verificarUsuario(String cedula, Connection conn) {
        return verificarTipoUsuario(cedula, conn);
    }

    // ==================== REGISTRO DE ESTUDIANTE ====================

    public void insertarDatos(Estudiante estudiante, Connection conn) {
        PreparedStatement ps = null;

        try {
            // Primero insertamos en la tabla Persona
            String sqlPersona = "INSERT INTO Persona (cedula, nombre, edad, genero, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sqlPersona);
            ps.setString(1, estudiante.getCedula());
            ps.setString(2, estudiante.getNombre());
            ps.setInt(3, estudiante.getEdad());
            ps.setString(4, estudiante.getGenero());
            ps.setString(5, estudiante.getDireccion());
            ps.setLong(6, estudiante.getTelefono());
            ps.setString(7, estudiante.getEmail());
            ps.executeUpdate();
            ps.close();

            // Luego insertamos en la tabla Estudiante
            String sqlEstudiante = "INSERT INTO Estudiante (cedula, nivel, paralelo, representante) VALUES (?, ?, ?, ?)";
            ps = conn.prepareStatement(sqlEstudiante);
            ps.setString(1, estudiante.getCedula());
            ps.setString(2, estudiante.getNivelEducativo());
            ps.setString(3, estudiante.getParalelo());
            ps.setString(4, estudiante.getRepresentante());
            ps.executeUpdate();

            System.out.println("Estudiante insertado exitosamente.");

        } catch (SQLException ex) {
            System.out.println("Error al insertar estudiante:");
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== REGISTRO DE ASIGNATURA ====================

    public int registrarAsignatura(String nombre, String codigo, int horasSemanales, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "INSERT INTO Asignatura (nombre, codigo, horas_semanales) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nombre);
            ps.setString(2, codigo);
            ps.setInt(3, horasSemanales);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException ex) {
            System.out.println("Error al registrar asignatura:");
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    // ==================== REGISTRO DE DOCENTE ====================

    public void insertarDocente(Docente docente, Connection conn, String horario, int idAsignatura) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 1️⃣ Verificar si la cédula ya existe en la tabla Persona
            String sqlVerificar = "SELECT cedula FROM Persona WHERE cedula = ?";
            ps = conn.prepareStatement(sqlVerificar);
            ps.setString(1, docente.getCedula());
            rs = ps.executeQuery();

            boolean cedulaExisteEnPersona = rs.next();
            rs.close();
            ps.close();

            // 2️⃣ Si NO existe en Persona, insertar primero ahí
            if (!cedulaExisteEnPersona) {
                String sqlPersona = "INSERT INTO Persona (cedula, nombre, edad, genero, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(sqlPersona);
                ps.setString(1, docente.getCedula());
                ps.setString(2, docente.getNombre());
                ps.setInt(3, docente.getEdad());
                ps.setString(4, docente.getGenero());
                ps.setString(5, docente.getDireccion());
                ps.setLong(6, docente.getTelefono());
                ps.setString(7, docente.getEmail());
                ps.executeUpdate();
                ps.close();
                System.out.println("Datos personales registrados en la tabla Persona.");
            } else {
                System.out.println("La cédula ya existe en la tabla Persona. Se omite el registro personal.");
            }

            // 3️⃣ Verificar si ya está registrado como Docente
            String sqlVerificarDocente = "SELECT cedula FROM Docente WHERE cedula = ?";
            ps = conn.prepareStatement(sqlVerificarDocente);
            ps.setString(1, docente.getCedula());
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("ERROR: Esta cédula ya está registrada como Docente.");
                rs.close();
                ps.close();
                return;
            }
            rs.close();
            ps.close();

            // 4️⃣ Insertar en la tabla Docente
            String sqlDocente = "INSERT INTO Docente (cedula, sueldo, jornada_laboral, horas_trabajadas, especialidad, titulo_academico, carga_horaria, horario_clases, asignatura_id, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sqlDocente);
            ps.setString(1, docente.getCedula());
            ps.setDouble(2, docente.getSueldoMensual());
            ps.setString(3, docente.getJornadaLaboral());
            ps.setInt(4, docente.getHorasTrabajadas());
            ps.setString(5, docente.getEspecialidad());
            ps.setString(6, docente.getTituloAcademico());
            ps.setInt(7, docente.getCargaHoraria());
            ps.setString(8, horario);
            ps.setInt(9, idAsignatura);
            ps.setString(10, "Activo");
            ps.executeUpdate();

            System.out.println("Docente insertado exitosamente.");

        } catch (SQLException ex) {
            System.out.println("Error al insertar docente:");
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // ==================== REGISTRO DE ADMINISTRATIVO ====================

    public void insertarAdministrativo(Administrativo admin, Connection conn) {
        PreparedStatement psPersona = null;
        PreparedStatement psAdmin = null;
        PreparedStatement psEstado = null;

        try {
            String sqlPersona = "INSERT INTO Persona (cedula, nombre, edad, genero, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?, ?)";
            psPersona = conn.prepareStatement(sqlPersona);
            psPersona.setString(1, admin.getCedula());
            psPersona.setString(2, admin.getNombre());
            psPersona.setInt(3, admin.getEdad());
            psPersona.setString(4, admin.getGenero());
            psPersona.setString(5, admin.getDireccion());
            psPersona.setLong(6, admin.getTelefono());
            psPersona.setString(7, admin.getEmail());
            psPersona.executeUpdate();

            String sqlAdmin = "INSERT INTO Administrativo (cedula, cargo, area, jornada_laboral, horas_trabajadas, sueldo) VALUES (?, ?, ?, ?, ?, ?)";
            psAdmin = conn.prepareStatement(sqlAdmin);
            psAdmin.setString(1, admin.getCedula());
            psAdmin.setString(2, admin.getCargo());
            psAdmin.setString(3, admin.getArea());
            psAdmin.setString(4, admin.getJornada());
            psAdmin.setInt(5, admin.getHoras());
            psAdmin.setDouble(6, admin.getSueldoMensual());
            psAdmin.executeUpdate();

            String sqlEstado = "INSERT INTO estadoempleabilidad (cedula, estado) VALUES (?, 'Activo')";
            psEstado = conn.prepareStatement(sqlEstado);
            psEstado.setString(1, admin.getCedula());
            psEstado.executeUpdate();

            System.out.println("Administrativo registrado exitosamente con estado: Activo");

        } catch (SQLException ex) {
            System.out.println("Error al insertar administrativo:");
            ex.printStackTrace();
        } finally {
            try {
                if (psPersona != null) psPersona.close();
                if (psAdmin != null) psAdmin.close();
                if (psEstado != null) psEstado.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // ==================== GESTIÓN DE NOTAS ====================

    public void insertarNota(String cedulaEstudiante, int asignaturaId, String parcial, double nota, String fechaRegistro, Connection conn) {
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO notas (cedula_estudiante, id_asignatura, parcial, nota, fecha_registro) VALUES (?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, asignaturaId);
            ps.setString(3, parcial);
            ps.setDouble(4, nota);
            ps.setString(5, fechaRegistro);
            ps.executeUpdate();

            System.out.println("Nota registrada exitosamente.");

        } catch (SQLException ex) {
            System.out.println("Error al insertar nota:");
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void actualizarNota(String cedulaEstudiante, int asignaturaId, String parcial, double nota, String fechaActualizacion, Connection conn) {
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE notas SET nota = ?, fecha_registro = ? WHERE cedula_estudiante = ? AND id_asignatura = ? AND parcial = ?";
            ps = conn.prepareStatement(sql);
            ps.setDouble(1, nota);
            ps.setString(2, fechaActualizacion);
            ps.setString(3, cedulaEstudiante);
            ps.setInt(4, asignaturaId);
            ps.setString(5, parcial);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("Nota actualizada exitosamente.");
            } else {
                System.out.println("No se encontró la nota para actualizar.");
            }

        } catch (SQLException ex) {
            System.out.println("Error al actualizar nota:");
            ex.printStackTrace();
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void verNotasEstudiante(String cedulaEstudiante, int idAsignatura, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT parcial, nota, fecha_registro FROM notas WHERE cedula_estudiante = ? AND id_asignatura = ? ORDER BY FIELD(parcial, 'Primer Parcial', 'Segundo Parcial', 'Tercer Parcial')";
            ps = conn.prepareStatement(sql);
            ps.setString(1, cedulaEstudiante);
            ps.setInt(2, idAsignatura);
            rs = ps.executeQuery();

            System.out.println("\n========================================");
            System.out.println("      NOTAS DEL ESTUDIANTE");
            System.out.println("========================================");

            boolean hayNotas = false;
            while (rs.next()) {
                hayNotas = true;
                String parcial = rs.getString("parcial");
                double nota = rs.getDouble("nota");
                String fecha = rs.getString("fecha_registro");
                System.out.printf("%-20s %.2f (Fecha: %s)%n", parcial, nota, fecha);
            }

            if (!hayNotas) {
                System.out.println("No hay notas registradas para este estudiante.");
            }

            System.out.println("========================================\n");

        } catch (SQLException ex) {
            System.out.println("Error al ver notas:");
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
