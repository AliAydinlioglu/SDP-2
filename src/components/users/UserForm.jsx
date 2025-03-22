import { useForm, FormProvider } from 'react-hook-form';
import LabelInput from '../LabelInput';
import { useNavigate } from 'react-router';

export default function UserForm({ user = {}, saveUser }) {
    
  const navigate = useNavigate();
  const methods = useForm({
    defaultValues: {
      voornaam: user.voornaam || '',
      achternaam: user.achternaam || '',
      email: user.email || '',
      gsm_nr: user.gsm_nr || '',
      geboorteDatum: user.geboorteDatum ? new Date(user.geboorteDatum).toISOString().split('T')[0] : '',
      straat: user.straat || '',
      huis_nr: user.huis_nr || '',
      stad: user.stad || '',
      postcode: user.postcode || '',
      land: user.land || '',
      rol: user.rol || '',
      actief: user.actief || true,
    },
  });

  const{
    handleSubmit,
    formState: {isValid, isSubmitting},
  } = methods;

  const onSubmit = async (data) => {
    if(!isValid) return;
    
    await saveUser({
      id: user?.id,
      ...data,
      geboorteDatum: new Date(data.geboorteDatum).toISOString(),
      actief: data.actief === 'true',
    },{
      throwOnError: true,
      
    },
    );
    navigate(user?.id ?`/users/${user.id}`: '/users');
  };

  return (
    <div className="form-container">
      <FormProvider {...methods}>
        <form onSubmit={handleSubmit(onSubmit)} className="user-form">
          <div className="form-row">
            <LabelInput 
              label="First Name" 
              name="voornaam" 
              type="text" 
              validationRules={{ required: 'First name is required' }} 
            />
            <LabelInput 
              label="Last Name" 
              name="achternaam" 
              type="text" 
              validationRules={{ required: 'Last name is required' }} 
            />
          </div>
          <div className="form-row">
            <LabelInput 
              label="Email" 
              name="email" 
              type="email" 
              validationRules={{ required: 'Email is required' }} 
            />
            <LabelInput 
              label="Phone" 
              name="gsm_nr" 
              type="text" 
              validationRules={{ required: 'Phone number is required' }} 
            />
          </div>
          <div className="form-row">
            <LabelInput 
              label="Birth Date" 
              name="geboorteDatum" 
              type="date" 
              validationRules={{ required: 'Birth date is required' }} 
            />
            <LabelInput 
              label="Street" 
              name="straat" 
              type="text" 
              validationRules={{ required: 'Street is required' }} 
            />
          </div>
          <div className="form-row">
            <LabelInput 
              label="House Number" 
              name="huis_nr" 
              type="text" 
              validationRules={{ required: 'House number is required' }} 
            />
            <LabelInput 
              label="City" 
              name="stad" 
              type="text" 
              validationRules={{ required: 'City is required' }} 
            />
          </div>
          <div className="form-row">
            <LabelInput 
              label="Postal Code" 
              name="postcode" 
              type="text" 
              validationRules={{ required: 'Postal code is required' }} 
            />
            <LabelInput 
              label="Country" 
              name="land" 
              type="text" 
              validationRules={{ required: 'Country is required' }} 
            />
          </div>
          <div className="form-row">
            <div className="mb-3">
              <label htmlFor="rol" className="form-label">Role</label>
              <select
                {...methods.register('rol', { 
                  required: 'Role is required',
                  validate: (value) => ['TECHNIEKER', 'MANAGER', 'VERANTWOORDELIJKE'].includes(value) || 'Invalid role',
                })}                id="rol"
                className="form-control"
                defaultValue={user.rol}
              >
                <option value="TECHNIEKER">Technieker</option>
                <option value="MANAGER">Manager</option>
                <option value="VERANTWOORDELIJKE">Verantwoordelijke</option>
              </select>
              {methods.formState.errors.rol && (
                <div className="form-text text-danger" data-cy="label_input_error">
                  {methods.formState.errors.rol.message}
                </div>
              )}
            </div>
            <div className="form-group radio-group">
              <label className="form-label">Active</label>
              <div className="form-check">
                <input
                  {...methods.register('actief')}
                  className="form-check-input"
                  type="radio"
                  value="true"
                  id="actiefTrue"
                  defaultChecked={user.actief === true}
                />
                <label className="form-check-label" htmlFor="actiefTrue">
                  Yes
                </label>
              </div>
              <div className="form-check">
                <input
                  {...methods.register('actief')}
                  className="form-check-input"
                  type="radio"
                  value="false"
                  id="actiefFalse"
                  defaultChecked={user.actief === false}
                />
                <label className="form-check-label" htmlFor="actiefFalse">
                  No
                </label>
              </div>
            </div>
            
          </div>
          <div className='form-row'>
            {!user?.id && (
              <LabelInput 
                label="Password" 
                name="password" 
                type="password" 
                validationRules={{ required: 'Password is required' }} 
              />
            )}
          </div>
          <button type="submit" className="btn btn-primary" disabled={isSubmitting}>Save</button>
        </form>
      </FormProvider>
    </div>
  );
}