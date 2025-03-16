import { useNavigate, useLocation } from 'react-router-dom';
import { useForm, FormProvider } from 'react-hook-form';
import LabelInput from '../components/LabelInput.jsx';
import { useCallback } from 'react';
import { useAuth } from '../context/auth.js';
import Error from '../components/Error';

const validationRules = {
  email: {
    required: 'Email is vereist',
    pattern: {
      value: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
      message: 'Email is ongeldig',
    },
  },
  password: {
    required: 'Wachtwoord is vereist',
    minLength: {
      value: 8,
      message: 'Wachtwoord moet minstens 8 tekens lang zijn',
    },
  },
};

export default function Login() {
  const { search} = useLocation();

  const {error, loading, login} = useAuth();

  const navigate = useNavigate();

  const methods = useForm({
    mode: 'onBlur',
    defaultValues: {
      email: 'geralt@gmail.com',
      password: '12345678',
    },
  });

  const { handleSubmit } = methods;

  const handleLogin = useCallback(
    async ({email, password}) =>{
      const loggedIn = await login(email, password);

      if (loggedIn) {
        const params = new URLSearchParams(search);
        navigate({
          pathname: params.get('redirect') || '/',
          replace: true,
        });
      }
    },
    [login, navigate, search],
  );

  return (
    
    <div className='form-container'>
      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(handleLogin)}>
          <h1>Log in</h1>
          <Error error={error} />
          <LabelInput
            label='Email'
            name='email'
            type='email'
            validationRules={validationRules.email}
            data-cy='email-input'
          />
          <LabelInput
            label='Wachtwoord'
            name='password'
            type='password'
            validationRules={validationRules.password}
            data-cy='password-input'
          />
          <div className='clearfix'>
            <div className='btn-group w-100'>
              <button
                type='submit'
                className='btn'
                disabled={loading}
                data-cy='submit-btn'
              >
                Log in
              </button>
            </div>
          </div>
        </form>
      </FormProvider>
    </div>
    
  );
};