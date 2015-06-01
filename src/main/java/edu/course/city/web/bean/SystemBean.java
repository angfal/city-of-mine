package edu.course.city.web.bean;

import edu.course.city.service.DatabaseService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.OutputStream;
import java.io.Serializable;

@SessionScoped
@ManagedBean
public class SystemBean implements Serializable {

    private static final long serialVersionUID = -3292406967125899042L;

    @ManagedProperty("#{databaseService}")
    private DatabaseService databaseService;

    public void downloadDatabaseDump() throws Exception {
        byte[] databaseDump = databaseService.getDatabaseDump();
        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext extContext = context.getExternalContext();
        extContext.responseReset();
        extContext.setResponseContentType("application/octet-stream");
        extContext.setResponseContentLength(databaseDump.length);
        extContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + databaseService.getDatabaseName() + ".sql\"");
        OutputStream outputStream = extContext.getResponseOutputStream();
        outputStream.write(databaseDump);
        context.responseComplete();
    }

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
}
