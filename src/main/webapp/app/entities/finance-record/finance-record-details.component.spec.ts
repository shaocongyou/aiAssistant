import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import FinanceRecordDetails from './finance-record-details.vue';
import FinanceRecordService from './finance-record.service';
import AlertService from '@/shared/alert/alert.service';

type FinanceRecordDetailsComponentType = InstanceType<typeof FinanceRecordDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const financeRecordSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('FinanceRecord Management Detail Component', () => {
    let financeRecordServiceStub: SinonStubbedInstance<FinanceRecordService>;
    let mountOptions: MountingOptions<FinanceRecordDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      financeRecordServiceStub = sinon.createStubInstance<FinanceRecordService>(FinanceRecordService);

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          financeRecordService: () => financeRecordServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        financeRecordServiceStub.find.resolves(financeRecordSample);
        route = {
          params: {
            financeRecordId: `${123}`,
          },
        };
        const wrapper = shallowMount(FinanceRecordDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.financeRecord).toMatchObject(financeRecordSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        financeRecordServiceStub.find.resolves(financeRecordSample);
        const wrapper = shallowMount(FinanceRecordDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
