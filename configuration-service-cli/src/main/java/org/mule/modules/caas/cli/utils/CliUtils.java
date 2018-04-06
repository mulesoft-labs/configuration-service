package org.mule.modules.caas.cli.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.io.Console;
import java.util.Arrays;

public class CliUtils {

    public static char[] readPassword() {
        Console c = System.console();
        return c.readPassword();
    }
    public static char[] readPassword(String prompt, Logger logger, int minLenght) {
        return readPassword(prompt, logger, minLenght, false);
    }
    public static char[] readPassword(String prompt, Logger logger, int minLenght, boolean optional) {
        char[] password = null;
        char[] ver = null;
        do {
            logger.info(prompt);
            password = readPassword();

            if (ArrayUtils.getLength(password) == 0 && optional) {
                return null;
            }

            if (ArrayUtils.getLength(password) < minLenght) {
                logger.info("Password should contain at least {} characters", minLenght);
            }

            logger.info("Please re-type for verification: ");
            ver = readPassword();

        } while (ArrayUtils.getLength(password) < minLenght || !Arrays.equals(password, ver));

        return password;
    }

    public static char[] readExistingPassword(String prompt, Logger logger, int minLength) {
        return readExistingPassword(prompt, logger, minLength, false);
    }

    public static char[] readExistingPassword(String prompt, Logger logger, int minLength, boolean optional) {

        char[] password = null;

        do {
            logger.info(prompt);
            password = readPassword();

            if (ArrayUtils.getLength(password) == 0 && optional) {
                return null;
            }

            if (ArrayUtils.getLength(password) < minLength) {
                logger.info("Password does not meet the min length {}", minLength);
            }

        } while (ArrayUtils.getLength(password) < minLength);

        return password;
    }

}
