import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';
const validationRules = {
  naam: {
    required: 'Site name is required',
  },
  verantw_id: {
    required: 'Manager is required',
  },
};

const EMPTY_SITE = {
  id: undefined,
  naam: '',
  verantw_id: '',
};

export default function SiteForm({ site = EMPTY_SITE, saveSite }) {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({
    mode: 'onBlur',
    defaultValues: {
      naam: site?.naam,
      verantw_id: site?.verantw_id,
      address: site?.address,
    },
  });
  const onSubmit = async (values) => {
    if (!isValid) return;

    await saveSite({
      id: site?.id,
      ...values,
    }, {
      throwOnError: false,
      onSuccess: () => navigate('/sites'),
    });
  };

  return (
    <div className='form-container'>
      <form onSubmit={handleSubmit(onSubmit)} className='site-form'>
        <div className='mb-3'>
          <label htmlFor='naam' className='form-label'>
            Site Naam
          </label>
          <input
            {...register('naam', validationRules.naam)}
            id='naam'
            name='naam'
            className='form-control'
          />
          {errors.naam ? <p className="form-text text-danger">{errors.naam.message}</p> : null}
        </div>

        <div className='mb-3'>
          <label htmlFor='verantw_id' className='form-label'>
            Verantwoordelijke
          </label>
          <input
            {...register('verantw_id', validationRules.verantw_id)}
            id='verantw_id'
            name='verantw_id'
            className='form-control'
          />
          {errors.verantw_id ? <p className="form-text text-danger">{errors.verantw_id.message}</p> : null}
        </div>

        <div className='clearfix'>
          <div className='btn-group float-end'>
            <button type='submit' className='btn btn-primary'>
              {site?.id
                ? 'Save site'
                : 'Add site'}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}