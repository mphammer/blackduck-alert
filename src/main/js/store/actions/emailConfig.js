import {
    EMAIL_CONFIG_FETCHING,
    EMAIL_CONFIG_FETCHED,
    EMAIL_CONFIG_UPDATE_ERROR,
    EMAIL_CONFIG_UPDATING,
    EMAIL_CONFIG_UPDATED,
    EMAIL_CONFIG_SHOW_ADVANCED,
    EMAIL_CONFIG_HIDE_ADVANCED
} from './types';

const CONFIG_URL = '/api/configuration/channel/email';

function scrubConfig(config) {
    return {
        mailSmtpHost: config.mailSmtpHost,
        mailSmtpUser: config.mailSmtpUser,
        mailSmtpPassword: config.mailSmtpPassword,
        mailSmtpPasswordIsSet: config.mailSmtpPasswordIsSet,
        mailSmtpPort: config.mailSmtpPort,
        mailSmtpConnectionTimeout: config.mailSmtpConnectionTimeout,
        mailSmtpTimeout: config.mailSmtpTimeout,
        mailSmtpWriteTimeout: config.mailSmtpWriteTimeout,
        mailSmtpFrom: config.mailSmtpFrom,
        mailSmtpLocalhost: config.mailSmtpLocalhost,
        mailSmtpLocalAddress: config.mailSmtpLocalAddress,
        mailSmtpLocalPort: config.mailSmtpLocalPort,
        mailSmtpEhlo: config.mailSmtpEhlo,
        mailSmtpAuth: config.mailSmtpAuth,
        mailSmtpAuthMechanisms: config.mailSmtpAuthMechanisms,
        mailSmtpAuthLoginDisable: config.mailSmtpAuthLoginDisable,
        mailSmtpAuthPlainDisable: config.mailSmtpAuthPlainDisable,
        mailSmtpAuthDigestMd5Disable: config.mailSmtpAuthDigestMd5Disable,
        mailSmtpAuthNtlmDisable: config.mailSmtpAuthNtlmDisable,
        mailSmtpAuthNtlmDomain: config.mailSmtpAuthNtlmDomain,
        mailSmtpAuthNtlmFlags: config.mailSmtpAuthNtlmFlags,
        mailSmtpAuthXoauth2Disable: config.mailSmtpAuthXoauth2Disable,
        mailSmtpSubmitter: config.mailSmtpSubmitter,
        mailSmtpDnsNotify: config.mailSmtpDnsNotify,
        mailSmtpDnsRet: config.mailSmtpDnsRet,
        mailSmtpAllow8bitmime: config.mailSmtpAllow8bitmime,
        mailSmtpSendPartial: config.mailSmtpSendPartial,
        mailSmtpSaslEnable: config.mailSmtpSaslEnable,
        mailSmtpSaslMechanisms: config.mailSmtpSaslMechanisms,
        mailSmtpSaslAuthorizationId: config.mailSmtpSaslAuthorizationId,
        mailSmtpSaslRealm: config.mailSmtpSaslRealm,
        mailSmtpSaslUseCanonicalHostname: config.mailSmtpSaslUseCanonicalHostname,
        mailSmtpQuitwait: config.mailSmtpQuitwait,
        mailSmtpReportSuccess: config.mailSmtpReportSuccess,
        mailSmtpSslEnable: config.mailSmtpSslEnable,
        mailSmtpSslCheckServerIdentity: config.mailSmtpSslCheckServerIdentity,
        mailSmtpSslTrust: config.mailSmtpSslTrust,
        mailSmtpSslProtocols: config.mailSmtpSslProtocols,
        mailSmtpSslCipherSuites: config.mailSmtpSslCipherSuites,
        mailSmtpStartTlsEnable: config.mailSmtpStartTlsEnable,
        mailSmtpStartTlsRequired: config.mailSmtpStartTlsRequired,
        mailSmtpProxyHost: config.mailSmtpProxyHost,
        mailSmtpProxyPort: config.mailSmtpProxyPort,
        mailSmtpSocksHost: config.mailSmtpSocksHost,
        mailSmtpSocksPort: config.mailSmtpSocksPort,
        mailSmtpMailExtension: config.mailSmtpMailExtension,
        mailSmtpUserSet: config.mailSmtpUserSet,
        mailSmtpNoopStrict: config.mailSmtpNoopStrict,
        id: (config.id?''+config.id: '')
    };
}

/**
 * Triggers Email Config Fetching reducer
 * @returns {{type}}
 */
function fetchingEmailConfig() {
    return {
        type: EMAIL_CONFIG_FETCHING
    };
}

/**
 * Triggers Email Config Fetched Reducer
 * @returns {{type}}
 */
function emailConfigFetched(config) {
    return {
        type: EMAIL_CONFIG_FETCHED,
        config: { ...scrubConfig(config) }
    };
}

/**
 * Triggers Config Error
 * @returns {{type}}
 */
function configError(message, errors) {
    return {
        type: EMAIL_CONFIG_UPDATE_ERROR,
        message,
        errors
    };
}

/**
 * Triggers Email Config Fetching reducer
 * @returns {{type}}
 */
function updatingEmailConfig() {
    return {
        type: EMAIL_CONFIG_UPDATING
    };
}

/**
 * Triggers Email Config Fetched Reducer
 * @returns {{type}}
 */
function emailConfigUpdated(config) {
    console.log("Email Config Updated", config);
    return {
        type: EMAIL_CONFIG_UPDATED,
        config: { ...scrubConfig(config) }
    };
}

export function toggleAdvancedEmailOptions(toggle) {
    if (toggle) {
        return { type: EMAIL_CONFIG_SHOW_ADVANCED };
    }
    return { type: EMAIL_CONFIG_HIDE_ADVANCED };
}

export function getEmailConfig() {
    return (dispatch, getState) => {
        dispatch(fetchingEmailConfig());
        const csrfToken = getState().session.csrfToken;
        fetch(CONFIG_URL, {
            credentials: 'include',
            headers: {
              'X-CSRF-TOKEN': csrfToken
            }
        })
            .then(response => response.json().then((body) => {
                if (body.length > 0) {
                    dispatch(emailConfigFetched(body[0]));
                } else {
                    dispatch(emailConfigFetched({}));
                }
            }))
            .catch(console.error);
    };
}

export function updateEmailConfig(config) {
    return (dispatch, getState) => {
        dispatch(updatingEmailConfig());
        const method = config.id ? 'PUT' : 'POST';
        const body = scrubConfig(config);
        const csrfToken = getState().session.csrfToken;
        fetch(CONFIG_URL, {
            credentials: 'include',
            method,
            body: JSON.stringify(body),
            headers: {
                'content-type': 'application/json',
                'X-CSRF-TOKEN': csrfToken
            }
        })
            .then((response) => {
                if (response.ok) {
                    response.json().then((data) => { dispatch(emailConfigUpdated({ ...config, id: data.id })); });
                } else {
                    response.json()
                        .then((data) => {
                            console.log('data', data.message);
                            switch (response.status) {
                                case 400:
                                    return dispatch(configError(data.message, data.errors));
                                case 412:
                                    return dispatch(configError(data.message, data.errors));
                                default:
                                    return dispatch(configError(data.message, null));
                            }
                        });
                }
            })
            .then(() => {
                dispatch(getEmailConfig());
            })
            .catch(console.error);
    };
}