/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.builder;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.ProcessStatus;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.Group;
import org.dspace.scripts.DSpaceCommandLineParameter;
import org.dspace.scripts.Process;
import org.dspace.scripts.service.ProcessService;

public class ProcessBuilder extends AbstractBuilder<Process, ProcessService> {

    private Process process;

    protected ProcessBuilder(Context context) {
        super(context);
    }

    public static ProcessBuilder createProcess(Context context, EPerson ePerson, String scriptName,
                                               List<DSpaceCommandLineParameter> parameters)
        throws SQLException {
        ProcessBuilder processBuilder = new ProcessBuilder(context);
        return processBuilder.create(context, ePerson, scriptName, parameters, null);
    }

    public static ProcessBuilder createProcess(Context context, EPerson ePerson, String scriptName,
                                               List<DSpaceCommandLineParameter> parameters,
                                               Set<Group> specialGroups)
        throws SQLException {
        ProcessBuilder processBuilder = new ProcessBuilder(context);
        return processBuilder.create(context, ePerson, scriptName, parameters, specialGroups);
    }

    private ProcessBuilder create(Context context, EPerson ePerson, String scriptName,
                                  List<DSpaceCommandLineParameter> parameters, final Set<Group> specialGroups)
        throws SQLException {
        this.context = context;
        this.process = processService.create(context, ePerson, scriptName, parameters, specialGroups);
        this.process.setProcessStatus(ProcessStatus.SCHEDULED);
        return this;
    }

    public ProcessBuilder withProcessStatus(ProcessStatus processStatus) {
        process.setProcessStatus(processStatus);
        return this;
    }

    public ProcessBuilder withCreationTime(Instant creationTime) {
        process.setCreationTime(creationTime);
        return this;
    }

    /**
     * Create a process with the given start & end dates. The dates are expected to be in YYYY-MM-DD format.
     * @param startTime date in YYYY-MM-DD format used to represent start time
     * @param endTime date in YYYY-MM-DD format used to represent end time
     * @return ProcessBuilder
     */
    public ProcessBuilder withStartAndEndTime(String startTime, String endTime) {
        process.setStartTime(startTime == null ? null : LocalDate.parse(startTime).atStartOfDay()
                                                                 .atZone(ZoneId.systemDefault()).toInstant());
        process.setFinishedTime(endTime == null ? null : LocalDate.parse(endTime).atStartOfDay()
                                                                  .atZone(ZoneId.systemDefault()).toInstant());
        return this;
    }

    @Override
    public void cleanup() throws Exception {
        try (Context c = new Context()) {
            c.setDispatcher("noindex");
            c.turnOffAuthorisationSystem();
            // Ensure object and any related objects are reloaded before checking to see what needs cleanup
            process = c.reloadEntity(process);
            if (process != null) {
                delete(c, process);
            }
            c.complete();
            indexingService.commit();
        }
    }

    @Override
    public Process build() {
        try {
            processService.update(context, process);
            context.dispatchEvents();
            indexingService.commit();
        } catch (Exception e) {
            return null;
        }
        return process;
    }

    @Override
    protected ProcessService getService() {
        return processService;
    }

    @Override
    public void delete(Context c, Process dso) throws Exception {
        if (dso != null) {
            getService().delete(c, dso);
        }
    }

    public static void deleteProcess(Integer integer) throws SQLException, IOException {
        if (integer == null) {
            return;
        }
        try (Context c = new Context()) {
            c.turnOffAuthorisationSystem();
            Process process = processService.find(c, integer);
            if (process != null) {
                try {
                    processService.delete(c, process);
                } catch (AuthorizeException e) {
                    // cannot occur, just wrap it to make the compiler happy
                    throw new RuntimeException(e);
                }
            }
            c.complete();
        }
    }
}
