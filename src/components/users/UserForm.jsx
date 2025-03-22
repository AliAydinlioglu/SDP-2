import { useForm, FormProvider } from 'react-hook-form';
import LabelInput from '../LabelInput';

export default function UserForm({ user = {}, saveUser }) {
  const methods = useForm({
    defaultValues: {
      voornaam: user.voornaam || '',
      achternaam: user.achternaam || '',
      email: user.email || '',
      gsm_nr: user.gsm_nr || '',
      geboorteDatum: user.geboorteDatum || '',
      straat: user.straat || '',
      huis_nr: user.huis_nr || '',
      stad: user.stad || '',
      postcode: user.postcode || '',
      land: user.land || '',
      rol: user.rol || '',
    },
  });

  const onSubmit = (data) => {
    saveUser(data);
  };

  return (
    <div className="form-container">
      <FormProvider {...methods}>
        <form onSubmit={methods.handleSubmit(onSubmit)}>
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
          <LabelInput 
            label="Role" 
            name="rol" 
            type="text" 
            validationRules={{ required: 'Role is required' }} 
          />
          <button type="submit" className="btn btn-primary">Save User</button>
        </form>
      </FormProvider>
    </div>
  );
}