import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router';

const validationRules = {
  name: {
    required: 'Site name is required',
  },
  manager: {
    required: 'Manager is required',
  },
  address: {
    required: 'Address is required',
  },
};

const EMPTY_SITE = {
  id: undefined,
  name: '',
  manager: '',
  address: '',
};

export default function SiteForm({ site = EMPTY_SITE, saveSite }) {
  const navigate = useNavigate();
  const { register, handleSubmit, formState: { errors, isValid } } = useForm({
    mode: 'onBlur',
    defaultValues: {
      name: site?.name,
      manager: site?.manager,
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
          <label htmlFor='name' className='form-label'>
            Site Name
          </label>
          <input
            {...register('name', validationRules.name)}
            id='name'
            name='name'
            className='form-control'
          />
          {errors.name ? <p className="form-text text-danger">{errors.name.message}</p> : null}
        </div>

        <div className='mb-3'>
          <label htmlFor='manager' className='form-label'>
            Manager
          </label>
          <input
            {...register('manager', validationRules.manager)}
            id='manager'
            name='manager'
            className='form-control'
          />
          {errors.manager ? <p className="form-text text-danger">{errors.manager.message}</p> : null}
        </div>

        <div className='mb-3'>
          <label htmlFor='address' className='form-label'>
            Address
          </label>
          <input
            {...register('address', validationRules.address)}
            id='address'
            name='address'
            className='form-control'
          />
          {errors.address ? <p className="form-text text-danger">{errors.address.message}</p> : null}
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