enum textCodes {
    NOJWT = "no jwt",
    INVALIDJWT = "invalid jwt",
    NOACCESS = "no access",
    NOUSERFOUND = "no user found",
    WRONGPASSWORD = "wrong password",
    MISSINGDATA = "missing data",
    INVALIDDATA = "invalid data",
    USERMISSING = "user missing",
    DUPLICATE = "duplicate",
    INVALIDEMAIL = '"email" must be a valid email',
    NODATA = '"value" must have at least 1 key',
    EMAILALREADYEXISTS = "email already exists",
    SHORTPASSWORD = '"password" length must be at least 8 characters long',
    SITENOTFOUND = "site not found",
}

export = textCodes;
