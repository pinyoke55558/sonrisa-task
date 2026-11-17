import Keycloak from 'keycloak-js'

export const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL ?? 'http://localhost:8180',
  realm: import.meta.env.VITE_KEYCLOAK_REALM ?? 'sonrise',
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID ?? 'alerts-frontend',
})

export function initKeycloak() {
  return keycloak.init({
    onLoad: 'login-required',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  })
}

export function signOut() {
  return keycloak.logout({ redirectUri: window.location.origin })
}
