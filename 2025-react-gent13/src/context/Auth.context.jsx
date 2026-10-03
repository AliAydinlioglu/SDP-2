import { createContext, useState, useCallback, useMemo, useContext } from 'react';
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
  } = useSWR(token ? 'users/me' : null, api.getById);

  const {
    isMutating: loginLoading,
    error: loginError,
    trigger: doLogin,
  } = useSWRMutation('auth/login', api.post);

  const login = useCallback(
    async (email, password) =>{
      try {
        const response = await doLogin({
          email,
          password,
        });
        
        setToken(response.token);
        localStorage.setItem(JWT_TOKEN_KEY, response.token);

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

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};