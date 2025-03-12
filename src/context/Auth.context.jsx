import { createContext, useState, useCallback, useMemo } from 'react';
import useSWRMutation from 'swr/mutation';
import * as api from '../api/index';
import useSWR from 'swr';

export const JWT_TOKEN_KEY = 'jwtToken';
export const AuthContext = createContext();

export const AuthProvider = ({children}) =>{
  const [token, setToken] = useState(localStorage.getItem(JWT_TOKEN_KEY));
  
  const {
    data: klant,
    loading: klantLoading,
    error: klantError,
  } = useSWR(token ? 'users/me' : null, api.getById);

  const {
    isMutating: loginLoading,
    error: loginError,
    trigger: doLogin,
  } = useSWRMutation('auth/login', api.post);

  const login = useCallback(
    async (email, password) =>{
      try {
        const {token} = await doLogin({
          email,
          password,
        });
        
        setToken(token);

        localStorage.setItem(JWT_TOKEN_KEY, token);

        return true;
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [doLogin],
  );

  const logout = useCallback(() =>{
    setToken(null);
    localStorage.removeItem(JWT_TOKEN_KEY);
  }, []);

  const value = useMemo(
    () => ({
      klant,
      error: klantError || loginError,
      loading: klantLoading || loginLoading,
      isAuthed: Boolean(token),
      ready: !klantLoading,
      login,
      logout,
    }),
    [token, klant, klantLoading, klantError, login, loginLoading, loginError, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};