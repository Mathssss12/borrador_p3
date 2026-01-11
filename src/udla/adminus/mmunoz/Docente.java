package udla.adminus.mmunoz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Docente extends Empleado implements Informacion {
    private String especialidad;
    private String tituloAcademico;
    private int cargaHoraria;


    public Docente(String cedula, String nombre, int edad, String genero,
                   String direccion, long telefono, String email,
                   double sueldoMensual, String jornadaLaboral, int horasTrabajadas,
                   String especialidad, String tituloAcademico, int cargaHoraria) {
        super(cedula, nombre, edad, genero, direccion, telefono, email,
              sueldoMensual, jornadaLaboral, horasTrabajadas);
        this.especialidad = especialidad;
        this.tituloAcademico = tituloAcademico;
        this.cargaHoraria = cargaHoraria;
    }

    @Override
    public String mostrarInformacion() {
        return super.mostrarInformacion() +
                "Especialidad: " + this.especialidad + "\n" +
                "Título Académico: " + this.tituloAcademico + "\n" +
                "Carga Horaria: " + this.cargaHoraria + " horas\n";
    }

    @Override
    public void mostrarInformacionCompleta(String cedula, Connection conn) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT u.nombre_completo, u.edad, u.genero, u.direccion, u.telefono, u.email, " +
                        "d.especialidad, d.horario_atencion, d.sueldo, d.jornada_laboral, d.horas_trabajadas, " +
                        "a.nombre AS asignatura " +
                        "FROM Usuario u " +
                        "INNER JOIN Docente d ON u.cedula = d.cedula " +
                        "LEFT JOIN Asignatura a ON d.asignatura_id = a.id_asignatura " +
                        "WHERE u.cedula = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();

            if(rs.next()) {
                System.out.println("\n========================================");
                System.out.println("      MI INFORMACIÓN - DOCENTE");
                System.out.println("========================================");
                System.out.println("Cédula: " + cedula);
                System.out.println("Nombre: " + rs.getString("nombre_completo"));
                System.out.println("Edad: " + rs.getInt("edad"));
                System.out.println("Género: " + rs.getString("genero"));
                System.out.println("Dirección: " + rs.getString("direccion"));
                System.out.println("Teléfono: " + rs.getLong("telefono"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("\n--- DATOS LABORALES ---");
                System.out.println("Especialidad: " + rs.getString("especialidad"));
                System.out.println("Horario de Atención: " + rs.getString("horario_atencion"));
                String asignatura = rs.getString("asignatura");
                System.out.println("Asignatura: " + (asignatura != null ? asignatura : "Sin asignar"));
                System.out.println("Sueldo: $" + rs.getDouble("sueldo"));
                System.out.println("Jornada Laboral: " + rs.getString("jornada_laboral"));
                System.out.println("Horas Trabajadas: " + rs.getInt("horas_trabajadas") + " horas");
                System.out.println("========================================\n");
            }

        } catch(SQLException ex) {
            System.out.println("ERROR al mostrar informacion: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public double calcularNomina() {
        return getSueldoMensual();
    }

    // ==================== GETTERS Y SETTERS ====================

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTituloAcademico() {
        return tituloAcademico;
    }

    public void setTituloAcademico(String tituloAcademico) {
        this.tituloAcademico = tituloAcademico;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    // MÉTODOS AGREGADOS PARA CORREGIR ERRORES EN SQL.java

    public String getJornada() {
        return getJornadaLaboral();
    }

    public int getHoras() {
        return getHorasTrabajadas();
    }
}