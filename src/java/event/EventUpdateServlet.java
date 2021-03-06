/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package event;

import dressCode.DressCode;
import dressCode.DressCodeDAO;
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
 * @author patricio alberto
 */
@WebServlet(name = "EventUpdateServlet", urlPatterns = {"/EventUpdateServlet"})
public class EventUpdateServlet extends HttpServlet {

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

        /* establecer set de caracteres */
        request.setCharacterEncoding("UTF-8");

        /* definir conexion */
        Connection conexion = null;

        try {
            /////////////////////////////////////////
            // ESTABLECER CONEXION
            ///////////////////////////////////////// 

            conexion = ds.getConnection();

            EventDAO eventDAO = new EventDAO();
            eventDAO.setConexion(conexion);

            DressCodeDAO dressCodeDAO = new DressCodeDAO();
            dressCodeDAO.setConexion(conexion);

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
                if (access != 555 && access != 777) {
                    request.getRequestDispatcher("/ForbiddenServlet").forward(request, response);
                } else {

                    /* obtener los valores de session y asignar valores a la jsp */
                    request.setAttribute("userJsp", username);
                    request.setAttribute("access", access);

                    /////////////////////////////////////////
                    // DECLARAR VARIABLES DE INSTANCIA
                    ////////////////////////////////////////

                    Event event = new Event();

                    try {
                        /////////////////////////////////////////
                        // RECIBIR Y COMPROBAR PARAMETROS
                        ////////////////////////////////////////

                        String sidPlace = request.getParameter("idPlace");
                        String sidEvent = request.getParameter("idEvent");
                        String namePlace = request.getParameter("namePlace");
                        String tittle = request.getParameter("tittle");
                        String details = request.getParameter("details");
                        String dateBegin = request.getParameter("dateBegin");
                        String dateEnd = request.getParameter("dateEnd");
                        String urlImage = request.getParameter("urlImage");
                        String spoints = request.getParameter("points");
                        String srequest = request.getParameter("eventRequest");
                        String sidDressCode = request.getParameter("idDressCode");
                        String reason = request.getParameter("reason");

                        boolean error = false;

                        /* comprobar id place */
                        if (sidPlace == null || sidPlace.trim().equals("")) {
                            request.setAttribute("msgErrorIdPlace", "Error al recibir id de plaza.");
                            error = true;
                        } else {
                            event.setIdPlace(Integer.parseInt(sidPlace));
                        }

                        /* comprobar id event */
                        if (sidEvent == null || sidEvent.trim().equals("")) {
                            request.setAttribute("msgErrorIdEvent", "Error al recibir id de evento.");
                            error = true;
                        } else {
                            event.setIdEvent(Integer.parseInt(sidEvent));
                        }

                        /* comprobar namePlace */
                        if (namePlace == null || namePlace.trim().equals("")) {
                            request.setAttribute("msgErrorNamePlace", "Error al recibir nombre de plaza.");
                            error = true;
                        } else {
                            event.setNamePlace(namePlace);
                        }

                        /* comprobar tittle*/
                        if (tittle == null || tittle.trim().equals("")) {
                            request.setAttribute("msgErrorTittle", "Error al recibir título.");
                            error = true;
                        } else {
                            event.setTittle(tittle);
                        }

                        /* comprobar details */
                        if (details == null || details.trim().equals("")) {
                            request.setAttribute("msgErrorDetails", "Error al recibir detalles.");
                            error = true;
                        } else {
                            event.setDetails(details);
                        }

                        /* comprobar points */
                        if (spoints == null || spoints.trim().equals("")) {
                            request.setAttribute("msgErrorPoints", "Error: Debe ingresar puntos.");
                            error = true;
                        } else {
                            try {
                                event.setPoints(Integer.parseInt(spoints));
                                if (event.getPoints() < 0) {
                                    request.setAttribute("msgErrorPoints", "Error: Los puntos no pueden ser negativos.");
                                    error = true;
                                }
                            } catch (NumberFormatException n) {
                                request.setAttribute("msgErrorPoints", "Error: Los puntos deben ser numéricos.");
                                error = true;
                            }
                        }

                        /* comprobar url image*/
                        if (urlImage == null || urlImage.trim().equals("")) {
                            request.setAttribute("msgErrorUrlImage", "Error: Debe ingresar url de imagen.");
                            error = true;
                        } else {
                            event.setUrlImage(urlImage);
                        }

                        /* comprobar dateBegin */
                        if (dateBegin == null || dateBegin.trim().equals("")) {
                            request.setAttribute("msgErrorDateBegin", "Error al recibir feha de inicio.");
                            error = true;
                        } else {
                            /* comprobar dateEnd */
                            if (dateEnd == null || dateEnd.trim().equals("")) {
                                request.setAttribute("msgErrorDateEnd", "Error al recibir feha de término.");
                                error = true;
                            } else {
                                /* comparar fechas */
                                event.setDateBegin(dateBegin);
                                event.setDateEnd(dateEnd);
                                //System.out.println("Comparar fecha 1 y fecha 2: " + event.getDateBegin().compareTo(event.getDateEnd()));
                                if (event.getDateBegin().compareTo(event.getDateEnd()) >= 0) {
                                    request.setAttribute("msgErrorDate", "Error: La fecha de término debe ser mayor que la fecha de inicio.");
                                    error = true;
                                }
                            }
                        }

                        /* comprobar request */
                        if (srequest == null || srequest.trim().equals("")) {
                            request.setAttribute("msgErrorRequest", "Error al recibir la solicitud.");
                            error = true;
                        } else {
                            try {
                                event.setRequest(Integer.parseInt(srequest));
                            } catch (NumberFormatException n) {
                                request.setAttribute("msgErrorType", "Error al recibir la solicitud.");
                                error = true;
                            }
                        }

                        /* comprobar id dress code */
                        if (sidDressCode == null || sidDressCode.trim().equals("")) {
                            error = true;
                        } else {
                            try {
                                event.setIdDressCode(Integer.parseInt(sidDressCode));
                            } catch (NumberFormatException n) {
                                error = true;
                            }
                        }

                        /* comprobar reason */
                        if (reason == null || reason.trim().equals("")) {
                        } else {
                            event.setReason(reason);
                        }

                        if (!error) {
                            /* comprobar registros duplicados */
                            boolean find = eventDAO.findDuplicate(event);
                            if (find) {
                                request.setAttribute("msgErrorEvent", "Error: ya existe este evento. Compruebe utilizando otro título u otro rango de fechas.");
                            } else {
                                /* comprobar existencia */
                                Event aux = eventDAO.findByPlaceEvent(event);
                                if (aux != null) {
                                    eventDAO.update(event);
                                    request.setAttribute("msgOk", "Registro actualizado exitosamente! ");
                                } else {
                                    request.setAttribute("msgErrorFound", "Error: no existe el evento o ha sido eliminado mientras se actualizaba.");
                                }
                            }
                        }

                        /* obtener lista de codigos de vestir */
                        try {
                            Collection<DressCode> listDressCode = dressCodeDAO.getAll();
                            request.setAttribute("listDressCode", listDressCode);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        request.setAttribute("event", event);

                    } catch (Exception parameterException) {
                    } finally {
                        request.getRequestDispatcher("/event/eventUpdate.jsp").forward(request, response);
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
