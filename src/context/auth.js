import { useContext } from 'react';
import { AuthContext } from './Auth.context';

export const useAuth = () => useContext(AuthContext);