package com.grahamlea.glissmetrics.exampleapplication.services;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorisationRouterImpl implements AuthorisationRouter {

    private RemoteService[] remoteServices;
    private double[] distributionRates;

    public AuthorisationResult authorise(String request) throws NoServiceAvailableException, TimedOutException {
        RemoteService chosenService = null;
        double selector = Math.random();
        double distributionCounter = 0;
        for (int i = 0; i < distributionRates.length; i++) {
            distributionCounter += distributionRates[i];
            if (selector <= distributionCounter) {
                chosenService = remoteServices[i];
                break;
            }
        }
        if (chosenService != null) {
            AuthorisationResult result = chosenService.authorise(request);
            if (result == null)
                throw new TimedOutException();
            return result;
        }
        throw new NoServiceAvailableException();
    }

    @Required
    public void setRemoteServices(List<RemoteService> remoteServices) {
        this.remoteServices = remoteServices.toArray(new RemoteService[remoteServices.size()]);
    }

    @Required
    public void setDistributionRates(double[] distributionRates) {
        this.distributionRates = distributionRates;
    }
}
