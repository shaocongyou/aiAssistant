import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import FinanceRecordUpdate from './finance-record-update.vue';
import FinanceRecordService from './finance-record.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type FinanceRecordUpdateComponentType = InstanceType<typeof FinanceRecordUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const financeRecordSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<FinanceRecordUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('FinanceRecord Management Update Component', () => {
    let comp: FinanceRecordUpdateComponentType;
    let financeRecordServiceStub: SinonStubbedInstance<FinanceRecordService>;

    beforeEach(() => {
      route = {};
      financeRecordServiceStub = sinon.createStubInstance<FinanceRecordService>(FinanceRecordService);
      financeRecordServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          financeRecordService: () => financeRecordServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(FinanceRecordUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(FinanceRecordUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.financeRecord = financeRecordSample;
        financeRecordServiceStub.update.resolves(financeRecordSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(financeRecordServiceStub.update.calledWith(financeRecordSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        financeRecordServiceStub.create.resolves(entity);
        const wrapper = shallowMount(FinanceRecordUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.financeRecord = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(financeRecordServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        financeRecordServiceStub.find.resolves(financeRecordSample);
        financeRecordServiceStub.retrieve.resolves([financeRecordSample]);

        // WHEN
        route = {
          params: {
            financeRecordId: `${financeRecordSample.id}`,
          },
        };
        const wrapper = shallowMount(FinanceRecordUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.financeRecord).toMatchObject(financeRecordSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        financeRecordServiceStub.find.resolves(financeRecordSample);
        const wrapper = shallowMount(FinanceRecordUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
