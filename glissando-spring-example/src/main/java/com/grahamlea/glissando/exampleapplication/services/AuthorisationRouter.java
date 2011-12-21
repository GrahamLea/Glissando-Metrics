package com.grahamlea.glissando.exampleapplication.services;

public interface AuthorisationRouter {
    public AuthorisationResult authorise(String request) throws NoServiceAvailableException, TimedOutException;
}
