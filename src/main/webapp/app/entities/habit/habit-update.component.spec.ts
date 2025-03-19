import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import HabitUpdate from './habit-update.vue';
import HabitService from './habit.service';
import AlertService from '@/shared/alert/alert.service';

import UserService from '@/entities/user/user.service';

type HabitUpdateComponentType = InstanceType<typeof HabitUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const habitSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<HabitUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Habit Management Update Component', () => {
    let comp: HabitUpdateComponentType;
    let habitServiceStub: SinonStubbedInstance<HabitService>;

    beforeEach(() => {
      route = {};
      habitServiceStub = sinon.createStubInstance<HabitService>(HabitService);
      habitServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          habitService: () => habitServiceStub,

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

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(HabitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.habit = habitSample;
        habitServiceStub.update.resolves(habitSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(habitServiceStub.update.calledWith(habitSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        habitServiceStub.create.resolves(entity);
        const wrapper = shallowMount(HabitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.habit = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(habitServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        habitServiceStub.find.resolves(habitSample);
        habitServiceStub.retrieve.resolves([habitSample]);

        // WHEN
        route = {
          params: {
            habitId: `${habitSample.id}`,
          },
        };
        const wrapper = shallowMount(HabitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.habit).toMatchObject(habitSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        habitServiceStub.find.resolves(habitSample);
        const wrapper = shallowMount(HabitUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
