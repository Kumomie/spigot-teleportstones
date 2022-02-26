package kumomi.teleportstones.build;

import kumomi.teleportstones.build.structure.Scope;
import kumomi.teleportstones.storage.scope.ScopeManager;

public class ScopeValidator {
    public static enum ValidationStatus {
        ERROR //
        , NOT_RUN //
        , MISSING_SCOPE //
        , MISSING_NAME //
        , EMPTY_NAME //
        , INVALID_NAME //
        , RESERVED_NAME //
        , NAME_ALREADY_EXISTS //
        , MISSING_REACHABLE_SCOPES //
        , UNKNOWN_REACHABLE_SCOPE //
        , SUCCESS
    }

    private ValidationStatus status;
    private String message;

    public ScopeValidator() {
        this.status = ValidationStatus.NOT_RUN;
        this.message = "";
    }

    public boolean validateBeforeAdd(Scope scope) {

        if (scope == null) {
            this.status = ValidationStatus.MISSING_SCOPE;
            this.message = "Missing scope.";
            return false;
        }

        if (scope.getName() == null) {
            this.status = ValidationStatus.MISSING_NAME;
            this.message = "Missing name of scope.";
            return false;
        }

        if (scope.getName().isEmpty()) {
            this.status = ValidationStatus.EMPTY_NAME;
            this.message = "Name of scope is empty.";
            return false;
        }

        if (scope.getName().contains(" ")) {
            this.status = ValidationStatus.INVALID_NAME;
            this.message = "Name of scope contains white space.";
            return false;
        }

        if (ScopeManager.defaultScopes.contains(scope.getName())) {
            this.status = ValidationStatus.RESERVED_NAME;
            this.message = "Specified scope name " + scope.getName() + " is a reserved name for defaults.";
            return false;
        }

        if (ScopeManager.scopes.containsKey(scope.getName())) {
            this.status = ValidationStatus.NAME_ALREADY_EXISTS;
            this.message = "Specified scope name " + scope.getName() + " is already in use.";
            return false;
        }

        if (scope.getReachableScopes() == null) {
            this.status = ValidationStatus.MISSING_REACHABLE_SCOPES;
            this.message = "Missing reachable scopes.";
            return false;
        }

        // Reachable scopes can be empty.

        return true;
    }

    public boolean validateScopeManager() {

        for (Scope scope : ScopeManager.scopes.values()) {

            for (String reachableScope : scope.getReachableScopes()) {
                if (!ScopeManager.defaultScopes.contains(reachableScope) //
                        && !ScopeManager.scopes.containsKey(reachableScope)) {

                    this.status = ValidationStatus.MISSING_REACHABLE_SCOPES;
                    this.message = "Missing reachable scopes.";
                    return false;
                }
            }

        }

        this.message = "ScopeManager successfully validated.";
        this.status = ValidationStatus.SUCCESS;

        return true;
    }

    public String getMessage() {
        return message;
    }

    public ValidationStatus getStatus() {
        return status;
    }

}
