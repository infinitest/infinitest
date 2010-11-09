package org.infinitest.keys;

import org.infinitest.util.Icon;

public interface LicenseState
{
    String getLicenseName();

    String getSupportStatus();

    boolean isValid();

    // DEBT How about a icon lookup that uses the license state as a key?
    Icon getIcon();
}
