import { createContext, useState, useCallback, useMemo } from 'react';
import useSWRMutation from 'swr/mutation';
import * as api from '../api/index';
import useSWR from 'swr';

export const JWT_TOKEN_KEY = 'jwtToken';
export const AuthContext = createContext();

export const AuthProvider = ({children}) =>{
  const [token, setToken] = useState(localStorage.getItem(JWT_TOKEN_KEY));
  
  const {
    data: user,
    loading: userLoading,
    error: userError,
  } = useSWR(token ? 'users' : null, api.getById);

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
      user,
      error: userError || loginError,
      loading: userLoading || loginLoading,
      isAuthed: Boolean(token),
      ready: !userLoading,
      login,
      logout,
    }),
    [token, user, userLoading, userError, login, loginLoading, loginError, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};