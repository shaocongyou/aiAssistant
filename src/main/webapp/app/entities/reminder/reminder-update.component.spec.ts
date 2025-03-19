import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ReminderUpdate from './reminder-update.vue';
import ReminderService from './reminder.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';
import TaskService from '@/entities/task/task.service';

type ReminderUpdateComponentType = InstanceType<typeof ReminderUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const reminderSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ReminderUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Reminder Management Update Component', () => {
    let comp: ReminderUpdateComponentType;
    let reminderServiceStub: SinonStubbedInstance<ReminderService>;

    beforeEach(() => {
      route = {};
      reminderServiceStub = sinon.createStubInstance<ReminderService>(ReminderService);
      reminderServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          reminderService: () => reminderServiceStub,

          userService: () =>
            sinon.createStubInstance<UserService>(UserService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
          taskService: () =>
            sinon.createStubInstance<TaskService>(TaskService, {
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
        const wrapper = shallowMount(ReminderUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(ReminderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.reminder = reminderSample;
        reminderServiceStub.update.resolves(reminderSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(reminderServiceStub.update.calledWith(reminderSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        reminderServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ReminderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.reminder = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(reminderServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        reminderServiceStub.find.resolves(reminderSample);
        reminderServiceStub.retrieve.resolves([reminderSample]);

        // WHEN
        route = {
          params: {
            reminderId: `${reminderSample.id}`,
          },
        };
        const wrapper = shallowMount(ReminderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.reminder).toMatchObject(reminderSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        reminderServiceStub.find.resolves(reminderSample);
        const wrapper = shallowMount(ReminderUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
