/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package placeCategory;

import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 *
 * @author patricio
 */
@WebServlet(name = "PlaceCategoryMainServlet", urlPatterns = {"/PlaceCategoryMainServlet"})
public class PlaceCategoryMainServlet extends HttpServlet {

    @Resource(name = "jdbc/POINTEX1")
    private DataSource ds;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Connection conexion = null;

        try {
            //////////////////////////////////////////
            // ESTABLECER CONEXION
            //////////////////////////////////////////

            conexion = ds.getConnection();

            PlaceCategoryDAO placeCategoryDAO = new PlaceCategoryDAO();
            placeCategoryDAO.setConexion(conexion);

            //////////////////////////////////////////
            // COMPROBAR SESSION
            /////////////////////////////////////////
            try {
                /* recuperar sesion */
                HttpSession session = request.getSession(false);

                /* obtener parametros de session */
                int access = Integer.parseInt((String) session.getAttribute("access"));
                String username = (String) session.getAttribute("username");

                /* comprobar permisos de usuario */
                if (access != 777) {
                    request.getRequestDispatcher("/ForbiddenServlet").forward(request, response);
                } else {
                    /* obtener los valores de session y asignar valores a la jsp */
                    request.setAttribute("userJsp", username);
                    request.setAttribute("access", access);

                    try {
                        //////////////////////////////////////////
                        // RECIBIR Y COMPROBAR PARAMETROS
                        /////////////////////////////////////////

                        String btnDelRow = request.getParameter("btnDelRow");
                        String btnDelCol = request.getParameter("btnDelCol");

                        PlaceCategory placeCategory = new PlaceCategory();

                        boolean error = false;

                        //////////////////////////////////////////
                        // ELIMINAR POR REGISTRO
                        //////////////////////////////////////////
                        if (btnDelRow != null) {
                            /* comprobar id place */
                            try {
                                int idPlace = Integer.parseInt(request.getParameter("idPlace"));
                                placeCategory.setIdPlace(idPlace);
                            } catch (NumberFormatException n) {
                                error = true;
                            }
                            /* comprobar id category */
                            try {
                                int idCategory = Integer.parseInt(request.getParameter("idCategory"));
                                placeCategory.setIdCategory(idCategory);
                            } catch (NumberFormatException n) {
                                error = true;
                            }

                            if (!error) {
                                try {
                                    placeCategoryDAO.delete(placeCategory);
                                    request.setAttribute("msgDel", "Un registro ha sido eliminado.");
                                } catch (Exception referenceException) {
                                    request.setAttribute("msgErrorReference", "Error: No se puede eliminar, existen errores en la ejecución.");
                                }
                            }
                        }
                        //////////////////////////////////////////
                        // ELIMINAR VARIOS REGISTROS
                        //////////////////////////////////////////
                        if (btnDelCol != null) {
                            try {
                                String[] outerArray = request.getParameterValues("chk");
                                int cont = 0;
                                int i = 0;
                                while (outerArray[i] != null) {
                                    String string = outerArray[i];
                                    String[] parts = string.split("-");
                                    placeCategory.setIdPlace(Integer.parseInt(parts[0]));
                                    placeCategory.setIdCategory(Integer.parseInt(parts[1]));
                                    try {
                                        placeCategoryDAO.delete(placeCategory);
                                        cont++;
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                    if (cont == 1) {
                                        request.setAttribute("msgDel", "Un registro ha sido eliminado.");
                                    } else if (cont > 1) {
                                        request.setAttribute("msgDel", cont + " registros han sido eliminados");
                                    }
                                    i++;
                                }
                            } catch (Exception parameterException) {
                            }
                        }
                        //////////////////////////////////////////
                        // ESTABLECER ATRIBUTOS AL REQUEST
                        /////////////////////////////////////////

                        Collection<PlaceCategory> placeCategoryList = placeCategoryDAO.getAll();

                        if (placeCategoryList.size() == 1) {
                            request.setAttribute("msg", "1 registro encontrado en la base de datos.");
                        } else if (placeCategoryList.size() > 1) {
                            request.setAttribute("msg", placeCategoryList.size() + " registros encontrados en la base de datos.");
                        } else if (placeCategoryList.isEmpty()) {
                            request.setAttribute("msg", "No hay registros encontrado en la base de datos.");
                        }

                        request.setAttribute("list", placeCategoryList);

                    } catch (Exception parameterException) {
                    } finally {
                        request.getRequestDispatcher("/placeCategory/placeCategory.jsp").forward(request, response);
                    }
                }
            } catch (Exception sessionException) {
                /* enviar a la vista de login */
                System.out.println("no ha iniciado session");
                request.getRequestDispatcher("/login/login.jsp").forward(request, response);
            }
        } catch (Exception connectionException) {
            connectionException.printStackTrace();
        } finally {
            try {
                conexion.close();
            } catch (Exception noGestionar) {
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
